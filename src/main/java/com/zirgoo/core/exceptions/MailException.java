package com.zirgoo.core.exceptions;

/**
 * Created by kosztope on 20/02/14.
 */
public class MailException extends Exception {

    public MailException() { super(); }
    public MailException(String message) { super(message); }
    public MailException(String message, Throwable cause) { super(message, cause); }
    public MailException(Throwable cause) { super(cause); }
}
