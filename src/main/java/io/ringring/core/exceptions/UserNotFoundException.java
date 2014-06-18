package io.ringring.core.exceptions;

/**
 * Created by kosztope on 18/02/14.
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException() { super(); }
    public UserNotFoundException(String message) { super(message); }
    public UserNotFoundException(String message, Throwable cause) { super(message, cause); }
    public UserNotFoundException(Throwable cause) { super(cause); }
}
