package org.emailclient;

public class EmailNotificationException extends RuntimeException{

    public EmailNotificationException() {
        super();
    }

    public EmailNotificationException(String message) {
        super(message);
    }

    public EmailNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotificationException(Throwable cause) {
        super(cause);
    }

    protected EmailNotificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
