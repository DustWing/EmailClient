package org.emailclient;

public class EmailClientError implements IEmailSender<EmailNotification> {



    @Override
    public boolean send(final EmailNotification emailNotification) {

      throw new EmailNotificationException("Testing failed scenarios");

    }

}
