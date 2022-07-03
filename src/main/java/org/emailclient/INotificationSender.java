package org.emailclient;

public interface INotificationSender<T> {

    boolean send(T t);


}
