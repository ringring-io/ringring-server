package com.zirgoo.server;

import com.google.inject.AbstractModule;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.json.JSONConfiguration;

import com.zirgoo.core.Status;
import com.zirgoo.core.StatusResult;
import com.zirgoo.core.User;

import com.zirgoo.core.UserListResult;
import com.zirgoo.core.UserResult;
import com.zirgoo.server.config.ConfigManager;
import com.zirgoo.server.config.setup.PropertiesConfigManagerImpl;
import com.zirgoo.server.config.setup.guice.ConfigManagerModule;
import com.zirgoo.server.db.baseline.CreateBaseline;
import com.zirgoo.server.persistence.repositories.UserRepository;
import com.zirgoo.server.persistence.repositories.setup.guice.UserRepositoryModule;
import com.zirgoo.server.persistence.setup.guice.PersistenceModule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import static junit.framework.Assert.*;

/**
 * Created by kosztope on 27/01/14.
 */

public class UserRepositoryTest {

    static ZirgooServerApi zirgooServerApi;
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
        zirgooServerApi = configManagerModuleInjector.getInstance(ZirgooServerApi.class);
        zirgooServerApi.run();

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
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

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

        User registeredUser = userResult.getResult();
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

        assertNull(userResult.getResult());
    }

    @Test
    public void shouldNotRegisterUser() throws Exception {
        String email = "test.user.invalid.email.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

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

        assertNull(userResult.getResult());
    }

    @Test
    public void shouldActivateUser() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

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

        User registeredUser = userResult.getResult();
        assertEquals(email, registeredUser.getEmail());
        assertFalse(registeredUser.getIsActivated());
        assertFalse(registeredUser.getIsLoggedIn());

        // The activation code is not sent in the REST response. Get it from the email
        String activationCode = getActivationCodeFromEmailContent(message.getContent().toString());
        registeredUser.setActivationCode(activationCode);

        // Activate the user
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + registeredUser.getEmail());
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
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

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

        User registeredUser = userResult.getResult();
        assertEquals(email, registeredUser.getEmail());
        assertFalse(registeredUser.getIsActivated());
        assertFalse(registeredUser.getIsLoggedIn());

        // 1st try: email in the path is not matching to the user's email
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/test.user.other@test.com");
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, registeredUser);

        assertEquals(Status.INVALID_EMAIL, statusResult.getStatus());

        // 2nd try: Incorrect activation code
        registeredUser.setActivationCode("SOME_FALSE_ACTIVATION_CODE");

        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + registeredUser.getEmail());
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
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email1);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user1 = userResult.getResult();

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
        User user2 = userResult.getResult();

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
        User user3 = userResult.getResult();

        // Get third activation code from email
        MimeMessage message3 = getLastMail().getMimeMessage();
        String activationCode3 = getActivationCodeFromEmailContent(message3.getContent().toString());
        user3.setActivationCode(activationCode3);

        // Activate two users from the three (1st and 3rd)
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + user1.getEmail());
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, user1);
        assertEquals(Status.OKAY, statusResult.getStatus());

        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + user3.getEmail());
        statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, user3);
        assertEquals(Status.OKAY, statusResult.getStatus());

        // Tyring to get all users one by one
        // 1st user should registered and activated
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + email1);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertTrue(userResult.getResult().getIsActivated());

        // 2nd user should not be registered
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + email2);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);

        assertEquals(Status.USER_NOT_FOUND, userResult.getStatus());
        assertNull(userResult.getResult());

        // 3rd user should registered and activated
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + email3);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertTrue(userResult.getResult().getIsActivated());

        // 4th user never existed in the system
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + email4);
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(UserResult.class);
        assertEquals(Status.USER_NOT_FOUND, userResult.getStatus());
        assertNull(userResult.getResult());
    }

    @Test
    public void shouldGetUsersByEmailList() throws Exception {
        String email1 = "test.user.1@test.com";
        String email2 = "test.user.2@test.com";
        String email3 = "test.user.3@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email1);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        User user1 = userResult.getResult();

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
        User user2 = userResult.getResult();

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
        User user3 = userResult.getResult();

        // Get first activation code from email
        MimeMessage message3 = getLastMail().getMimeMessage();
        String activationCode3 = getActivationCodeFromEmailContent(message3.getContent().toString());
        user3.setActivationCode(activationCode3);

        // Activate two users from the three (1st and 3rd)
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + user1.getEmail());
        StatusResult statusResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(StatusResult.class, user1);
        assertEquals(Status.OKAY, statusResult.getStatus());

        webResource = client.resource(zirgooServerApi.getUrl() + "/user/" + user3.getEmail());
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

        webResource = client.resource(zirgooServerApi.getUrl() + "/user/list");
        UserListResult userListResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserListResult.class, emailsRequestHash);

        // Should be only two activated users from all of them
        assertEquals(2, userListResult.getResult().getUserList().size());
    }

    @Test
    public void shouldRenewActivationCode() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

        // Create request hash with email address
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.OKAY, userResult.getStatus());
        assertNotNull(userResult.getResult());

        // Get first activation code from email
        MimeMessage message1 = getLastMail().getMimeMessage();
        String firstActivationCode = getActivationCodeFromEmailContent(message1.getContent().toString());

        // Request to change the activation code
        webResource = client.resource(zirgooServerApi.getUrl() + "/user/renewactivationcode/" + email);
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
    public void shouldBadRequest() throws Exception {
        String email = "test.user.1@test.com";

        UserResult userResult;
        Client client = createClient();
        WebResource webResource = client.resource(zirgooServerApi.getUrl() + "/user");

        // Create request hash with email address using wrong key
        HashMap<String, String> requestHash = new HashMap<String, String>();
        requestHash.put("email_wrong_key", email);

        // First email registration
        userResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserResult.class, requestHash);
        assertEquals(Status.BAD_REQUEST, userResult.getStatus());
        assertNull(userResult.getResult());

        // Create request hash with email address
        HashMap<String, List<String>> emailsRequestHash = new HashMap<String, List<String>>();
        List<String> userEmails = Arrays.asList("test.user.1@test.com",
                "test.user.2@test.com",
                "test.user.3@test.com",
                "test.user.4@test.com");

        // Put emails list into requestHash with wrong hash key
        emailsRequestHash.put("emails_wrong_key", userEmails);

        webResource = client.resource(zirgooServerApi.getUrl() + "/user/list");
        UserListResult userListResult = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(UserListResult.class, emailsRequestHash);
        assertEquals(Status.BAD_REQUEST, userListResult.getStatus());
        assertNull(userListResult.getResult());
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
