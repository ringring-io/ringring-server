package com.zirgoo.server.persistence.repositories.setup;

import java.net.URLEncoder;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import com.zirgoo.core.User;

import com.google.inject.Inject;
import com.zirgoo.core.exceptions.InvalidActivationCodeException;
import com.zirgoo.core.exceptions.MailException;
import com.zirgoo.core.exceptions.UserAlreadyActivatedException;
import com.zirgoo.core.exceptions.UserNotFoundException;
import com.zirgoo.core.exceptions.MailException;
import com.zirgoo.server.config.ConfigManager;
import com.zirgoo.server.config.setup.PropertiesConfigManagerImpl;
import com.zirgoo.server.persistence.ConnectionManager;
import com.zirgoo.server.persistence.repositories.UserRepository;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by kosztope on 23/01/14.
 */
public class PlainSqlUserRepositoryImpl implements UserRepository {

    private final ConfigManager configManager;
    private final ConnectionManager connectionManager;

    @Inject
    public PlainSqlUserRepositoryImpl(ConnectionManager connectionManager, ConfigManager configManager) {
        this.connectionManager = connectionManager;
        this.configManager = configManager;
    }

    @Override
    public String getActivationCode(String email) throws Exception {
        String activationCode = null;
        Connection connection = connectionManager.getConnection();

        try {
            PreparedStatement query = connection.prepareStatement("SELECT activation_code FROM zirgoo_users WHERE email = LOWER(?)");
            query.clearParameters();
            query.setString(1, email);

            ResultSet rs = query.executeQuery();
            while(rs.next()) {
                activationCode = rs.getString(1);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return activationCode;
    }

    @Override
    public void update(User user) {
        Connection connection = connectionManager.getConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE zirgoo_users SET activation_code=?, is_activated=? WHERE email=LOWER(?)");
            stmt.clearParameters();

            stmt.setString(1, user.getActivationCode());
            stmt.setBoolean(2, user.getIsActivated());
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void dropUsers() {
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("TRUNCATE TABLE zirgoo_users");
            stmt.execute();

            stmt = connection.prepareStatement("TRUNCATE TABLE directory");
            stmt.execute();

            stmt = connection.prepareStatement("TRUNCATE TABLE directory_vars");
            stmt.execute();

            stmt = connection.prepareStatement("TRUNCATE TABLE directory_params");
            stmt.execute();

            stmt = connection.prepareStatement("TRUNCATE TABLE dialplan_extension");
            stmt.execute();

            stmt = connection.prepareStatement("TRUNCATE TABLE dialplan_condition");
            stmt.execute();

            stmt = connection.prepareStatement("TRUNCATE TABLE dialplan_actions");
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public User getUser(String email, boolean onlyActivatedUsers) throws Exception {
        User user = null;
        Connection connection = connectionManager.getConnection();

        try {
            String sql = "SELECT email, activation_code, is_activated, FALSE FROM zirgoo_users WHERE email = LOWER(?)";
            if(onlyActivatedUsers) sql = sql + " AND is_activated = TRUE";

            PreparedStatement query = connection.prepareStatement(sql);
            query.clearParameters();
            query.setString(1, email);

            ResultSet rs = query.executeQuery();
            while(rs.next()) {
                user = new User(
                        rs.getString(1),
                        "XXXXX", // do _NOT_ send the activation code directly
                        rs.getBoolean(3),
                        rs.getBoolean(4)
                );
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();

            throw new SQLException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return user;
    }

    @Override
    public List<User> getUsers(List<String> emailList, boolean onlyActivatedUsers) throws Exception{
        List<User> userList = new ArrayList<User>();
        Connection connection = connectionManager.getConnection();

        if (emailList.size() == 0) {
            return userList;
        }

        String sqlInEmails = "(";
        for (String email : emailList) {
            sqlInEmails = sqlInEmails + "'" + email.toLowerCase() + "',";
        }
        sqlInEmails = sqlInEmails + "null)";

        try {
            String sql = "SELECT email, activation_code, is_activated, FALSE FROM zirgoo_users WHERE email IN " + sqlInEmails;
            if(onlyActivatedUsers) sql = sql + " AND is_activated = TRUE";

            PreparedStatement query = connection.prepareStatement(sql);
            query.clearParameters();

            ResultSet rs = query.executeQuery();
            while(rs.next()) {
                userList.add(new User(
                        rs.getString(1),
                        "XXXXX", // do _NOT_ send the activation code directly
                        rs.getBoolean(3),
                        rs.getBoolean(4)
                ));
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();

            throw new SQLException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return userList;
    }

    @Override
    public User register(String email) throws Exception {
        Connection connection = connectionManager.getConnection();
        try {
            if(!isValidEmailAddress(email))
                throw new AddressException();

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO zirgoo_users (email, activation_code, is_activated) VALUES(LOWER(?), SUBSTRING(MD5(RAND()) FROM 1 FOR ?), FALSE)");
            stmt.clearParameters();
            stmt.setString(1, email.toLowerCase());
            stmt.setInt(2, configManager.getActivationCodeLength());

            stmt.executeUpdate();

            // Email activation code
            String activationCode = getActivationCode(email.toLowerCase());
            String body = configManager.getActivationCodeBody() + activationCode;
            sendMail(email.toLowerCase(), configManager.getActivationCodeSubject(), body);

        } catch (AddressException e) {
            throw new AddressException();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLIntegrityConstraintViolationException(e);
        } catch (MessagingException e) {
            e.printStackTrace();

            throw new MailException(e);
        } catch (SQLException e) {
            e.printStackTrace();

            throw new SQLException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        }

        return getUser(email, false);
    }

    @Override
    public void activate(User user) throws Exception {
        Connection connection = connectionManager.getConnection();
        try {
            if(!isValidEmailAddress(user.getEmail()))
                throw new AddressException();

            User dbUser = getUser(user.getEmail(), false);

            if(dbUser == null)
                throw new UserNotFoundException();

            // User is already activated, update the SIP password
            if(dbUser.getIsActivated()) {

                PreparedStatement stmt = connection.prepareStatement("UPDATE directory_params SET param_value = ? WHERE directory_id = (SELECT id FROM directory WHERE zirgoo_user_id = (SELECT id FROM zirgoo_users WHERE activation_code = ? AND email = LOWER(?))) AND param_name = 'password'");
                stmt.clearParameters();
                stmt.setString(1, user.getActivationCode());
                stmt.setString(2, user.getActivationCode());
                stmt.setString(3, user.getEmail());

                // No updated rows; activation codes are not matching
                if (stmt.executeUpdate() == 0) {
                    throw new InvalidActivationCodeException();
                }

                throw new UserAlreadyActivatedException("User is already activated. Data has been updated");
            }

            // Update zirgoo_users_table
            PreparedStatement stmt = connection.prepareStatement("UPDATE zirgoo_users SET is_activated = true WHERE email = LOWER(?) AND activation_code = ?");

            stmt.clearParameters();
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getActivationCode());

            // No updated rows; activation codes are not matching
            if (stmt.executeUpdate() == 0) {
                throw new InvalidActivationCodeException();
            }

            // Insert into directory table
            stmt = connection.prepareStatement("INSERT INTO directory (zirgoo_user_id, username, domain, domain_id) VALUES ((SELECT id FROM zirgoo_users WHERE email = LOWER(?)), ?, '', (SELECT id FROM directory_domains WHERE domain_name = ?))");
            stmt.clearParameters();
            stmt.setString(1, user.getEmail().toLowerCase());
            stmt.setString(2, URLEncoder.encode(user.getEmail().toLowerCase(), "UTF-8"));
            stmt.setString(3, configManager.getSipDomain());

            stmt.executeUpdate();

            // Insert into directory_vars table (context)
            stmt = connection.prepareStatement("INSERT INTO directory_vars (directory_id, var_name, var_value) VALUES ((SELECT id FROM directory WHERE username = ?), 'user_context', LOWER(?))");
            stmt.clearParameters();
            stmt.setString(1, URLEncoder.encode(user.getEmail().toLowerCase(), "UTF-8"));
            stmt.setString(2, configManager.getSipContext());

            stmt.executeUpdate();

            // Insert into directory_vars table (internal_caller_id_name)
            stmt = connection.prepareStatement("INSERT INTO directory_vars (directory_id, var_name, var_value) VALUES ((SELECT id FROM directory WHERE username = ?), 'internal_caller_id_name', LOWER(?))");
            stmt.clearParameters();
            stmt.setString(1, URLEncoder.encode(user.getEmail().toLowerCase(), "UTF-8"));
            stmt.setString(2, user.getEmail().toLowerCase());

            stmt.executeUpdate();

            // Insert into directory_params table (password) */
            stmt = connection.prepareStatement("INSERT INTO directory_params (directory_id, param_name, param_value) VALUES ((SELECT id FROM directory WHERE username = ?), 'password', ?)");
            stmt.clearParameters();
            stmt.setString(1, URLEncoder.encode(user.getEmail().toLowerCase(), "UTF-8"));
            stmt.setString(2, user.getActivationCode());

            stmt.executeUpdate();

            // Insert into directory_params table (password) */
            stmt = connection.prepareStatement("INSERT INTO directory_params (directory_id, param_name, param_value) VALUES ((SELECT id FROM directory WHERE username = ?), 'dial-string', '{presence_id=${dialed_user}@${dialed_domain}}${sofia_contact(${dialed_user}@${dialed_domain})}')");
            stmt.clearParameters();
            stmt.setString(1, URLEncoder.encode(user.getEmail().toLowerCase(), "UTF-8"));

            stmt.executeUpdate();

            /* INSERT INTO dialplan_extension (Email based unique extension) */
            stmt = connection.prepareStatement("INSERT INTO dialplan_extension (context_id, name, `continue`, weight) VALUES ((SELECT context_id FROM dialplan_context WHERE context = ?), LOWER(?), '', 0)");
            stmt.clearParameters();
            stmt.setString(1, configManager.getSipContext());
            stmt.setString(2, user.getEmail().toLowerCase());

            stmt.executeUpdate();

            /* INSERT INTO dialplan_condition (Regexp match to bridge extensions) */
            stmt = connection.prepareStatement("INSERT INTO dialplan_condition (extension_id, field, expression, weight) VALUES ((SELECT extension_id FROM dialplan_extension WHERE name = LOWER(?)), 'destination_number', CONCAT(CONCAT('^', ?), '$'), 10)");
            stmt.clearParameters();
            stmt.setString(1, user.getEmail().toLowerCase());
            stmt.setString(2, URLEncoder.encode(user.getEmail().toLowerCase(), "UTF-8"));

            stmt.executeUpdate();

            /* INSERT INTO dialplan_action (call_timeout) */
            stmt = connection.prepareStatement("INSERT INTO dialplan_actions (condition_id, application, data, type, weight) VALUES ((SELECT condition_id FROM dialplan_condition WHERE extension_id = (SELECT extension_id FROM dialplan_extension WHERE name = LOWER(?))), 'set', 'call_timeout=30', 'action', 10)");
            stmt.clearParameters();
            stmt.setString(1, user.getEmail().toLowerCase());

            stmt.executeUpdate();

             /* INSERT INTO dialplan_action (bridge) */
            stmt = connection.prepareStatement("INSERT INTO dialplan_actions (condition_id, application, data, type, weight) VALUES ((SELECT condition_id FROM dialplan_condition WHERE extension_id = (SELECT extension_id FROM dialplan_extension WHERE name = LOWER(?))), 'bridge', CONCAT(CONCAT(CONCAT('user/', ?),'@'), ?), 'action', 20)");
            stmt.clearParameters();
            stmt.setString(1, user.getEmail().toLowerCase());
            stmt.setString(2, URLEncoder.encode(user.getEmail().toLowerCase(), "UTF-8"));
            stmt.setString(3, configManager.getSipDomain());

            stmt.executeUpdate();

             /* INSERT INTO dialplan_action (hangup) */
            stmt = connection.prepareStatement("INSERT INTO dialplan_actions (condition_id, application, data, type, weight) VALUES ((SELECT condition_id FROM dialplan_condition WHERE extension_id = (SELECT extension_id FROM dialplan_extension WHERE name = LOWER(?))), 'hangup', '', 'action', 30)");
            stmt.clearParameters();
            stmt.setString(1, user.getEmail().toLowerCase());

            stmt.executeUpdate();

        } catch (AddressException e) {
            throw new AddressException();
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (InvalidActivationCodeException e) {
            throw new InvalidActivationCodeException();
        } catch (UserAlreadyActivatedException e) {
            throw new UserAlreadyActivatedException();
        } catch (SQLException e) {
            e.printStackTrace();

            throw new SQLException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void renewActivationCode(String email) throws Exception {
        Connection connection = connectionManager.getConnection();
        try {
            if(!isValidEmailAddress(email))
                throw new AddressException();

            PreparedStatement stmt = connection.prepareStatement("UPDATE zirgoo_users SET activation_code = SUBSTRING(MD5(RAND()) FROM 1 FOR ?) WHERE email = LOWER(?)");
            stmt.clearParameters();
            stmt.setInt(1, configManager.getActivationCodeLength());
            stmt.setString(2, email.toLowerCase());

            stmt.executeUpdate();

            // Email the new activation code
            String activationCode = getActivationCode(email.toLowerCase());

            String body = configManager.getRenewActivationCodeBody() + activationCode;
            sendMail(email.toLowerCase(), configManager.getActivationCodeSubject(), body);

        } catch (AddressException e) {
            throw new AddressException();
        } catch (MessagingException e) {
            e.printStackTrace();

            throw new MailException(e);
        } catch (SQLException e) {
            e.printStackTrace();

            throw new SQLException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        }
    }

    public static boolean isValidEmailAddress(String aEmailAddress){
        if (aEmailAddress == null) return false;
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(aEmailAddress);
            if (! hasNameAndDomain(aEmailAddress)) {
                result = false;
            }
        }
        catch (AddressException ex){
            result = false;
        }
        return result;
    }

    private static boolean hasNameAndDomain(String aEmailAddress) {
        String[] tokens = aEmailAddress.split("@");
        return (
                tokens.length == 2 &&
                tokens[0].length() > 0 &&
                tokens[1].length() > 0);
    }

    private void sendMail(String to, String subject, String body) throws Exception {

        // Set mail session
        Session session = Session.getInstance(configManager.getProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configManager.getSmtpUser(), configManager.getSmtpPassword());
                    }
                });

        // Setup message properties
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(configManager.getSmtpFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        // Send
        Transport.send(message);
    }
}
