package com.zirgoo.core.exceptions;

/**
 * Created by kosztope on 19/02/14.
 */
public class EmailAlreadyRegisteredException extends Exception {

    public EmailAlreadyRegisteredException() { super(); }
    public EmailAlreadyRegisteredException(String message) { super(message); }
    public EmailAlreadyRegisteredException(String message, Throwable cause) { super(message, cause); }
    public EmailAlreadyRegisteredException(Throwable cause) { super(cause); }
}
