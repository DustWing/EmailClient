package org.emailclient;

public class EmailClientError implements INotificationSender<EmailNotification> {



    @Override
    public boolean send(final EmailNotification emailNotification) {

      throw new EmailNotificationException("Testing failed scenarios");

    }

}
