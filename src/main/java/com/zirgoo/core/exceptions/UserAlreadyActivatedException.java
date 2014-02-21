package com.zirgoo.core.exceptions;

/**
 * Created by kosztope on 19/02/14.
 */
public class UserAlreadyActivatedException extends Exception {

    public UserAlreadyActivatedException() { super(); }
    public UserAlreadyActivatedException(String message) { super(message); }
    public UserAlreadyActivatedException(String message, Throwable cause) { super(message, cause); }
    public UserAlreadyActivatedException(Throwable cause) { super(cause); }
}
