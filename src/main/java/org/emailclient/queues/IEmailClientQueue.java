package org.emailclient.queues;

import org.emailclient.EmailNotification;
import org.emailclient.INotificationSender;

public interface IEmailClientQueue<T> {
    void add(
            INotificationSender<T> emailSender, EmailNotification notification
    );

    void start();

    void resume();

    void pause();

    void shutdown();
}
