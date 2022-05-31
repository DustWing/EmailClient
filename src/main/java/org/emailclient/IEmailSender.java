package org.emailclient;

import java.util.concurrent.CompletableFuture;

public interface IEmailSender {

    CompletableFuture<Void> sendAsync(EmailNotification emailNotification);

    void send(EmailNotification notification);


}
