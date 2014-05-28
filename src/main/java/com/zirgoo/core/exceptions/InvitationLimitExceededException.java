package com.zirgoo.core.exceptions;

/**
 * Created by kosztope on 19/02/14.
 */
public class InvitationLimitExceededException extends Exception {

    public InvitationLimitExceededException() { super(); }
    public InvitationLimitExceededException(String message) { super(message); }
    public InvitationLimitExceededException(String message, Throwable cause) { super(message, cause); }
    public InvitationLimitExceededException(Throwable cause) { super(cause); }
}
