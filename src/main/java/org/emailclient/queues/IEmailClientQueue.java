package org.emailclient.queues;

import org.emailclient.EmailNotification;
import org.emailclient.IEmailSender;

public interface IEmailClientQueue<T> {
    void add(
            IEmailSender<T> emailSender, EmailNotification notification
    );

    void start();

    void resume();

    void pause();

    void shutdown();
}
