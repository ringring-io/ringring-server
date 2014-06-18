package io.ringring.core.exceptions;

/**
 * Created by kosztope on 19/02/14.
 */
public class InvitationLimitNotExceededException extends Exception {

    public InvitationLimitNotExceededException() { super(); }
    public InvitationLimitNotExceededException(String message) { super(message); }
    public InvitationLimitNotExceededException(String message, Throwable cause) { super(message, cause); }
    public InvitationLimitNotExceededException(Throwable cause) { super(cause); }
}
