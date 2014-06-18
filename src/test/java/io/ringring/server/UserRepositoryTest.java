package io.ringring.server;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.json.JSONConfiguration;

import io.ringring.core.Status;
import io.ringring.core.StatusResult;
import io.ringring.core.User;

import io.ringring.core.UserListResult;
import io.ringring.core.UserResult;
import io.ringring.server.config.ConfigManager;
import io.ringring.server.config.setup.PropertiesConfigManagerImpl;
import io.ringring.server.config.setup.guice.ConfigManagerModule;
import io.ringring.server.db.baseline.CreateBaseline;
import io.ringring.server.persistence.repositories.UserRepository;
import io.ringring.server.persistence.repositories.setup.guice.UserRepositoryModule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

/**
 * Created by kosztope on 27/01/14.
 */

public class UserRepositoryTest {

    static ringringServerApi ringringServerApi;
    static UserRepository userRepository;
    static ConfigManager configManager;
    static Wiser wiser;

    @BeforeClass
    public static void runServer() throws Exception {

        // Migrate database to the latest version
        Injector configManagerModuleInjector = Guice.createInjector(new ConfigManagerModule());
        CreateBaseline createBaseline = configManagerModuleInjector.getInstance(CreateBaseline.class);
        createBaseline.create();

        // Run service
        ringringServerApi = configManagerModuleInjector.getInstance(ringringServerApi.class);
        ringringServerApi.run();

        // Create direct db connection to the user repository
        Injector userRepositoryInjector = Guice.createInjector(new UserRepositoryModule());
        userRepository = userRepositoryInjector.getInstance(UserRepository.class);

        // Create config manager to direct access to the config
        configManager = new PropertiesConfigManagerImpl();

        // Start fake SMTP server
        wiser = new Wiser();
        wiser.setHostname(configManager.getSmtpHost());
        wiser.setPort(configManager.getSmtpPort());
        wiser.start();
    }

    @Before
    public void cleanDatabase() throws Exception {
        userRepository.dropUsers();
        userRepository.dropInvites();
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                          .accept(MediaType.APPLICATION_JSON_TYPE)
                          .post(UserResult.class, requestHash);

        // Check if email sent to the user
        MimeMessage message = getLastMail().getMimeMessage();
        assertEquals(configManager.getSmtpFrom(), message.getFrom()[0].toString());
        assertEquals(email, message.getRecipients(Message.RecipientType.TO)[0].toString());

        // User needs to be registered
        assertEquals(Status.OKAY, userResult.getStatus());
        assertTrue(userResult.isSuccess());

        User registeredUser = userResult.getUser();
        assertEquals(email, registeredUser.getEmail());
        assertFalse(registeredUser.getIsActivated());
        assertFalse(registeredUser.getIsLoggedIn());

        // Try to register the same email again
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                          .accept(MediaType.APPLICATION_JSON_TYPE)
                          .post(UserResult.class, requestHash);

        // Should be failed with email already registered exception
        assertEquals(Status.EMAIL_ALREADY_REGISTERED, userResult.getStatus());
        assertFalse(userResult.isSuccess());

        assertNull(userResult.getUser());
    }

    @Test
    public void shouldNotRegisterUser() throws Exception {
        String email = "test.user.invalid.email.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);

        // Should be failed with invalid email exception
        assertEquals(Status.INVALID_EMAIL, userResult.getStatus());
        assertFalse(userResult.isSuccess());

        assertNull(userResult.getUser());
    }

    @Test
    public void shouldActivateUser() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);

        // Check if email sent to the user
        MimeMessage message = getLastMail().getMimeMessage();
        assertEquals(configManager.getSmtpFrom(), message.getFrom()[0].toString());
        assertEquals(email, message.getRecipients(Message.RecipientType.TO)[0].toString());

        // User needs to registered
        assertEquals(Status.OKAY, userResult.getStatus());
        assertTrue(userResult.isSuccess());

        User registeredUser = userResult.getUser();
        assertEquals(email, registeredUser.getEmail());
        assertFalse(registeredUser.getIsActivated());
        assertFalse(registeredUser.getIsLoggedIn());

        // The activation code is not sent in the REST response. Get it from the email
        String activationCode = getActivationCodeFromEmailContent(message.getContent().toString());
        registeredUser.setActivationCode(activationCode);

        // Activate the user
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + registeredUser.getEmail());
        StatusResult statusresult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, registeredUser);

        assertEquals(Status.OKAY, statusresult.getStatus());
    }

    @Test
    public void shouldNotActivateUser() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);

        // User needs to registered
        assertEquals(Status.OKAY, userResult.getStatus());
        assertTrue(userResult.isSuccess());

        // Check if email sent to the user
        MimeMessage message = getLastMail().getMimeMessage();
        assertEquals(configManager.getSmtpFrom(), message.getFrom()[0].toString());
        assertEquals(email, message.getRecipients(Message.RecipientType.TO)[0].toString());

        User registeredUser = userResult.getUser();
        assertEquals(email, registeredUser.getEmail());
        assertFalse(registeredUser.getIsActivated());
        assertFalse(registeredUser.getIsLoggedIn());

        // 1st try: email in the path is not matching to the user's email
        webResource = client.resource(ringringServerApi.getUrl() + "/user/test.user.other@test.com");
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, registeredUser);

        assertEquals(Status.INVALID_EMAIL, statusResult.getStatus());

        // 2nd try: Incorrect activation code
        registeredUser.setActivationCode("SOME_FALSE_ACTIVATION_CODE");

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + registeredUser.getEmail());
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, registeredUser);

        assertEquals(Status.INVALID_ACTIVATION_CODE, statusResult.getStatus());
    }

    @Test
    public void shouldGetUserByEmail() throws Exception {
        String email1 = "test.user.1@test.com";
        String email2 = "test.user.2@test.com";
        String email3 = "test.user.3@test.com";
        String email4 = "test.user.4@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email1);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user1 = userResult.getUser();

        // Get first activation code from email
        MimeMessage message1 = getLastMail().getMimeMessage();
        String activationCode1 = getActivationCodeFromEmailContent(message1.getContent().toString());
        user1.setActivationCode(activationCode1);

        // Second email registration
        requestHash.clear();
        requestHash.put("email", email2);

        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user2 = userResult.getUser();

        // Get second activation code from email
        MimeMessage message2 = getLastMail().getMimeMessage();
        String activationCode2 = getActivationCodeFromEmailContent(message2.getContent().toString());
        user2.setActivationCode(activationCode2);

        // Third email registration
        requestHash.clear();
        requestHash.put("email", email3);

        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user3 = userResult.getUser();

        // Get third activation code from email
        MimeMessage message3 = getLastMail().getMimeMessage();
        String activationCode3 = getActivationCodeFromEmailContent(message3.getContent().toString());
        user3.setActivationCode(activationCode3);

        // Activate two users from the three (1st and 3rd)
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + user1.getEmail());
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, user1);
        assertEquals(Status.OKAY, statusResult.getStatus());

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + user3.getEmail());
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, user3);
        assertEquals(Status.OKAY, statusResult.getStatus());

        // Tyring to get all users one by one
        // 1st user should registered and activated
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + email1);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertTrue(userResult.getUser().getIsActivated());

        // 2nd user should not be registered
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + email2);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);

        assertEquals(Status.USER_NOT_FOUND, userResult.getStatus());
        assertNull(userResult.getUser());

        // 3rd user should registered and activated
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + email3);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertTrue(userResult.getUser().getIsActivated());

        // 4th user never existed in the system
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + email4);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);
        assertEquals(Status.USER_NOT_FOUND, userResult.getStatus());
        assertNull(userResult.getUser());
    }

    @Test
    public void shouldGetUsersByEmailList() throws Exception {
        String email1 = "test.user.1@test.com";
        String email2 = "test.user.2@test.com";
        String email3 = "test.user.3@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email1);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user1 = userResult.getUser();

        // Get first activation code from email
        MimeMessage message1 = getLastMail().getMimeMessage();
        String activationCode1 = getActivationCodeFromEmailContent(message1.getContent().toString());
        user1.setActivationCode(activationCode1);

        // Second email registration
        requestHash.clear();
        requestHash.put("email", email2);

        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user2 = userResult.getUser();

        // Get second activation code from email
        MimeMessage message2 = getLastMail().getMimeMessage();
        String activationCode2 = getActivationCodeFromEmailContent(message2.getContent().toString());
        user2.setActivationCode(activationCode2);

        // Third email registration
        requestHash.clear();
        requestHash.put("email", email3);

        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user3 = userResult.getUser();

        // Get first activation code from email
        MimeMessage message3 = getLastMail().getMimeMessage();
        String activationCode3 = getActivationCodeFromEmailContent(message3.getContent().toString());
        user3.setActivationCode(activationCode3);

        // Activate two users from the three (1st and 3rd)
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + user1.getEmail());
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, user1);
        assertEquals(Status.OKAY, statusResult.getStatus());

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + user3.getEmail());
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, user3);
        assertEquals(Status.OKAY, statusResult.getStatus());

        // Get the user list
        // Create request hash with email address
        HashMap<String, List<String>> emailsRequestHash = new HashMap<String, List<String>>();

        List<String> userEmails = Arrays.asList("test.user.1@test.com",
            "test.user.2@test.com",
            "test.user.3@test.com",
            "test.user.4@test.com");
        emailsRequestHash.put("emails", userEmails);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/list");
        UserListResult userListResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserListResult.class, emailsRequestHash);

        // Should be only two activated users from all of them
        assertEquals(2, userListResult.getUsers().size());
    }

    @Test
    public void shouldRenewActivationCode() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertNotNull(userResult.getUser());

        // Get first activation code from email
        MimeMessage message1 = getLastMail().getMimeMessage();
        String firstActivationCode = getActivationCodeFromEmailContent(message1.getContent().toString());

        // Request to change the activation code
        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + email + "/renewactivationcode");
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(StatusResult.class);
        assertEquals(Status.OKAY, statusResult.getStatus());

        // Get first activation code from email
        MimeMessage message2 = getLastMail().getMimeMessage();
        String secondActivationCode = getActivationCodeFromEmailContent(message2.getContent().toString());

        // The new activation code should not be the same as the previous one
        assertFalse(firstActivationCode.equals(secondActivationCode));
    }

    @Test
    public void shouldInviteUser() throws Exception {
        String fromEmail = "test.user.1@test.com";
        String toEmail = "test.user.2@test.com";

        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", fromEmail);

        // First email registration
        UserResult userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertNotNull(userResult.getUser());

        // Invit an other user
        HashMap<String, String> inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("from_email", fromEmail);
        inviteRequestHash.put("to_email", toEmail);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + toEmail + "/invite");
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.OKAY, statusResult.getStatus());

        // Check if email sent to the user
        MimeMessage message = getLastMail().getMimeMessage();
        assertEquals(configManager.getSmtpFrom(), message.getFrom()[0].toString());
        assertEquals(toEmail, message.getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    public void shouldNotInviteUser() throws Exception {
        String from_email = "test.user.1@test.com";
        String from_email_invalid = "test.user.1.invalid.email.com";
        String to_email = "test.user.2@test.com";
        String to_email_invalid = "test.user.2.invalid.email.com";

        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", from_email);

        // First email registration
        UserResult userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertNotNull(userResult.getUser());

        // Invite with wrong key without to_email
        HashMap<String, String> inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("from_email", from_email);
        inviteRequestHash.put("from_email", from_email);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + to_email + "/invite");
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.BAD_REQUEST, statusResult.getStatus());

        // Invite with invalid to_email key
        inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("from_email", from_email);
        inviteRequestHash.put("to_email", from_email);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + to_email + "/invite");
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.INVALID_EMAIL, statusResult.getStatus());

        // Invite with invalid email_from address
        inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("from_email", from_email_invalid);
        inviteRequestHash.put("to_email", to_email);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + to_email + "/invite");
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.INVALID_EMAIL, statusResult.getStatus());

        // Invite with invalid email_to address
        inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("from_email", from_email);
        inviteRequestHash.put("to_email", to_email_invalid);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + to_email_invalid + "/invite");
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.INVALID_EMAIL, statusResult.getStatus());

        // Invite with unregistered user
        inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("from_email", to_email);
        inviteRequestHash.put("to_email", from_email);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + from_email + "/invite");
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.USER_NOT_FOUND, statusResult.getStatus());

        // Invite already registered email
        inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("from_email", from_email);
        inviteRequestHash.put("to_email", from_email);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + from_email + "/invite");
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.EMAIL_ALREADY_REGISTERED, statusResult.getStatus());
    }

    @Test
    public void shouldBadRequest() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(ringringServerApi.getUrl() + "/user");

        // Create request hash with email address using wrong key
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email_wrong_key", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.BAD_REQUEST, userResult.getStatus());
        assertNull(userResult.getUser());

        // Create request hash with email address
        HashMap<String, List<String>> emailsRequestHash = new HashMap<String, List<String>>();
        List<String> userEmails = Arrays.asList("test.user.1@test.com",
                "test.user.2@test.com",
                "test.user.3@test.com",
                "test.user.4@test.com");

        // Put emails list into requestHash with wrong hash key
        emailsRequestHash.put("emails_wrong_key", userEmails);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/list");
        UserListResult userListResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserListResult.class, emailsRequestHash);
        assertEquals(Status.BAD_REQUEST, userListResult.getStatus());
        assertNull(userListResult.getUsers());

        // Invite email with wrong key
        HashMap<String, String> inviteRequestHash = new HashMap<String, String>();
        inviteRequestHash.put("email_wrong_key", email);
        inviteRequestHash.put("to_email", email);

        webResource = client.resource(ringringServerApi.getUrl() + "/user/" + email + "/invite");
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(StatusResult.class, inviteRequestHash);
        assertEquals(Status.BAD_REQUEST, statusResult.getStatus());
    }

    private Client createClient() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
                Boolean.TRUE);
        return Client.create(clientConfig);
    }

    private WiserMessage getLastMail() {
        List<WiserMessage> wiserMessages = wiser.getMessages();

        return wiserMessages.get(wiserMessages.size() - 1);
    }

    private String getActivationCodeFromEmailContent(String emailContent) {
        int startIndex = emailContent.lastIndexOf(':') + 1;
        int activationCodeLength = configManager.getActivationCodeLength();

        // Get activation code from the email
        String activationCode = emailContent.substring(startIndex, startIndex + activationCodeLength);

        // Check if the code has the right lenght
        assertEquals(activationCodeLength, activationCode.length());

        return activationCode;
    }
}
