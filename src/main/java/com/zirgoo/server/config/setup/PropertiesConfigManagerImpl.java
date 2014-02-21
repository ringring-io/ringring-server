package com.zirgoo.server.config.setup;

import com.google.inject.name.Names;
import com.zirgoo.server.config.ConfigManager;
import com.zirgoo.server.persistence.setup.guice.PersistenceModule;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by kosztope on 24/01/14.
 */
public class PropertiesConfigManagerImpl implements ConfigManager {
    private String propertyResource = "/server.properties";

    private Properties properties;

    private String serverHost;
    private int serverPort;

    private String databaseDriver;
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    private String sipDomain;
    private String sipContext;

    private String smtpHost;
    private int smtpPort;
    private String smtpAuth;
    private String smtpUser;
    private String smtpPassword;
    private String smtpFrom;
    private String smtpRealname;

    private String activationCodeSubject;
    private String activationCodeBody;

    private String renewActivationCodeSubject;
    private String renewActivationCodeBody;

    private int activationCodeLength;

    public PropertiesConfigManagerImpl() {
        properties = new Properties();

        // Read config from property file
        try {
            properties.load(PersistenceModule.class.getResourceAsStream(propertyResource));
        } catch (IOException e) {
            throw new Error("Unable find resource", e);
        }

        try {
            serverHost = properties.getProperty("server.host");
            serverPort = Integer.parseInt(properties.getProperty("server.port"));

            databaseDriver = properties.getProperty("db.driver");
            databaseUrl = properties.getProperty("db.url");
            databaseUser = properties.getProperty("db.user");
            databasePassword = properties.getProperty("db.password");

            sipDomain = properties.getProperty("sip.domain");
            sipContext = properties.getProperty("sip.context");

            smtpHost = properties.getProperty("mail.smtp.host");
            smtpPort = Integer.parseInt(properties.getProperty("mail.smtp.port"));
            smtpAuth = properties.getProperty("mail.smtp.auth");
            smtpUser = properties.getProperty("mail.smtp.user");
            smtpPassword = properties.getProperty("mail.smtp.password");
            smtpFrom = properties.getProperty("mail.smtp.from");
            smtpRealname = properties.getProperty("mail.smtp.realname");

            activationCodeSubject = properties.getProperty("mail.activationcode.subject");
            activationCodeBody = properties.getProperty("mail.activationcode.body");
            renewActivationCodeSubject = properties.getProperty("mail.renewactivationcode.subject");
            renewActivationCodeBody = properties.getProperty("mail.renewactivationcode.body");

            activationCodeLength = Integer.parseInt(properties.getProperty("misc.activationcode.length"));

        } catch (Exception e) {
            throw new Error("Error during reading properties from " + properties, e);
        }
    }

    @Override
    public Properties getProperties() { return properties; }

    @Override
    public String getServerHost() {
        return serverHost;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public String getDatabaseDriver() {
        return databaseDriver;
    }

    @Override
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    @Override
    public String getDatabaseUser() {
        return databaseUser;
    }

    @Override
    public String getDatabasePassword() {
        return databasePassword;
    }

    @Override
    public String getSipDomain() { return sipDomain; }

    @Override
    public String getSipContext() { return sipContext; }

    @Override
    public String getSmtpHost() { return smtpHost; }

    @Override
    public int getSmtpPort() { return smtpPort; }

    @Override
    public String getSmtpAuth() { return smtpAuth; }

    @Override
    public String getSmtpUser() { return smtpUser; }

    @Override
    public String getSmtpPassword() { return smtpPassword; }

    @Override
    public String getSmtpFrom() { return smtpFrom; }

    @Override
    public String getSmtpRealname() { return smtpRealname; }

    @Override
    public String getActivationCodeSubject() { return activationCodeSubject; }

    @Override
    public String getActivationCodeBody() { return activationCodeBody; }

    @Override
    public String getRenewActivationCodeSubject() { return renewActivationCodeSubject; }

    @Override
    public String getRenewActivationCodeBody() { return renewActivationCodeBody; }

    @Override
    public int getActivationCodeLength() { return activationCodeLength; }

    @Override
    public String toString() {
        return "ConfigManager{" + '\n'
                + " server.host=" + serverHost + '\n'
                + ",server.port=" + serverPort + '\n'
                + ",db.driver=" + databaseDriver + '\n'
                + ",db.url=" + databaseUrl + '\n'
                + ",db.user=" + databaseUser + '\n'
                + ",db.password=XXX" + '\n'
                + ",sip.domain=" + sipDomain + '\n'
                + ",sip.context=" + sipContext + '\n'
                + ",smtp.host=" + smtpHost + '\n'
                + ",smtp.port=" + smtpPort + '\n'
                + ",smtp.auth=" + smtpAuth + '\n'
                + ",smtp.user=" + smtpUser + '\n'
                + ",smtp.password=XXX" + '\n'
                + ",smtp.from=" + smtpFrom + '\n'
                + ",smtp.realname=" + smtpRealname + '\n'
                + ",mail.activationcode.subject=" + activationCodeSubject + '\n'
                + ",mail.activationcode.body=" + activationCodeBody + '\n'
                + ",mail.renewactivationcode.subject=" + renewActivationCodeSubject + '\n'
                + ",mail.renewactivationcode.body=" + renewActivationCodeBody + '\n'
                + ",misc.activationcode.length=" + activationCodeLength + '\n'
                + "}";
    }
}
