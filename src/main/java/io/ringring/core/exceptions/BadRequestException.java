package io.ringring.core.exceptions;

/**
 * Created by peterkosztolanyi on 23/02/2014.
 */
public class BadRequestException extends Exception {

    public BadRequestException() { super(); }
    public BadRequestException(String message) { super(message); }
    public BadRequestException(String message, Throwable cause) { super(message, cause); }
    public BadRequestException(Throwable cause) { super(cause); }
}
