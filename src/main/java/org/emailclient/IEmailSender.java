package org.emailclient;

public interface IEmailSender<T> {

    boolean send(T t);


}
