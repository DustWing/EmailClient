package org.emailclient;

public class EmailClientPass implements INotificationSender<EmailNotification> {



    @Override
    public boolean send(final EmailNotification emailNotification) {

      return true;

    }

}
