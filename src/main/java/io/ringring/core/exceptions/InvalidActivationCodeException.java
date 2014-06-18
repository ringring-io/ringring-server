package io.ringring.core.exceptions;

/**
 * Created by kosztope on 19/02/14.
 */
public class InvalidActivationCodeException extends Exception {

    public InvalidActivationCodeException() { super(); }
    public InvalidActivationCodeException(String message) { super(message); }
    public InvalidActivationCodeException(String message, Throwable cause) { super(message, cause); }
    public InvalidActivationCodeException(Throwable cause) { super(cause); }
}
