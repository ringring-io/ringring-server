package com.zirgoo.server.resources;

import javax.mail.internet.AddressException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import com.google.inject.Inject;

import com.zirgoo.core.Status;
import com.zirgoo.core.StatusResult;
import com.zirgoo.core.User;
import com.zirgoo.core.UserResult;
import com.zirgoo.core.UserListResult;
import com.zirgoo.core.exceptions.BadRequestException;
import com.zirgoo.core.exceptions.InvalidActivationCodeException;
import com.zirgoo.core.exceptions.MailException;
import com.zirgoo.core.exceptions.UserAlreadyActivatedException;
import com.zirgoo.core.exceptions.UserNotFoundException;
import com.zirgoo.server.persistence.repositories.UserRepository;

/**
 * Created by kosztope on 23/01/14.
 */

@Path("/user")
public class UserResource {

    private final UserRepository userRepository;

    @Inject
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GET
    @Path("{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserResult getUser(@PathParam("email") String email) {
        User user = null;
        Status status = Status.OKAY;

        try {
            user = userRepository.getUser(email, true);
        }
        catch (SQLException e) { status = Status.INTERNAL_DATABASE_ERROR; }
        catch (Exception e) { status = Status.INTERNAL_APPLICATION_ERROR; }
        if (user == null) { status = Status.USER_NOT_FOUND; }

        return new UserResult(user, status);
    }

    @POST
    @Path("/list/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserListResult getUsers(HashMap<String, List<String>> requestHash) {
        List<User> users = null;
        Status status = Status.OKAY;

        try {
            List<String> emails = requestHash.get("emails");
            if (emails == null) {
                throw new BadRequestException();
            }

            users = userRepository.getUsers(emails, true);
            if (users == null || users.size() == 0) {
                status = Status.USER_NOT_FOUND;
            }
        }
        catch (BadRequestException e) { status = Status.BAD_REQUEST; }
        catch (SQLException e) { status = Status.INTERNAL_DATABASE_ERROR; }
        catch (Exception e) { status = Status.INTERNAL_APPLICATION_ERROR; }

        return new UserListResult(users, status);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserResult register(HashMap<String, String> requestHash) {
        User user = null;
        Status status = Status.OKAY;

        try {
            String email = requestHash.get("email");
            if (email == null) {
                throw new BadRequestException();
            }

            user = userRepository.register(email);
        }
        catch (BadRequestException e) { status = Status.BAD_REQUEST; }
        catch (AddressException e) { status = Status.INVALID_EMAIL; }
        catch (SQLIntegrityConstraintViolationException e) { status = Status.EMAIL_ALREADY_REGISTERED; }
        catch (MailException e) { status = Status.INTERNAL_SMTP_ERROR; }
        catch (SQLException e) { status = Status.INTERNAL_DATABASE_ERROR; }
        catch (Exception e) { status = Status.INTERNAL_APPLICATION_ERROR; }

        return new UserResult(user, status);
    }

    @PUT
    @Path("{email}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResult activate(User user, @PathParam("email") String email) {
        Status status = Status.OKAY;

        if (!email.equals(user.getEmail())) {
            return new StatusResult(Status.INVALID_EMAIL);
        }

        try {
            userRepository.activate(user);
        }
        catch (AddressException e) { status = Status.INVALID_EMAIL; }
        catch (UserNotFoundException e) { status = Status.USER_NOT_FOUND; }
        catch (InvalidActivationCodeException e) { status = Status.INVALID_ACTIVATION_CODE; }
        catch (UserAlreadyActivatedException e) { status = Status.USER_ALREADY_ACTIVATED; }
        catch (SQLException e) { status = Status.INTERNAL_DATABASE_ERROR; }
        catch (Exception e) { status = Status.INTERNAL_APPLICATION_ERROR; }

        return new StatusResult(status);
    }

    @GET
    @Path("/renewactivationcode/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResult renewActivationCode(@PathParam("email") String email) {
        Status status = Status.OKAY;

        try {
            userRepository.renewActivationCode(email);
        }
        catch (AddressException e) { status = Status.INVALID_EMAIL; }
        catch (MailException e) { status = Status.INTERNAL_SMTP_ERROR; }
        catch (SQLException e) { status = Status.INTERNAL_DATABASE_ERROR; }
        catch (Exception e) { status = Status.INTERNAL_APPLICATION_ERROR; }

        return new StatusResult(status);
    }
}
