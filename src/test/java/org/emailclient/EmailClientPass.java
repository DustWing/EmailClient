package org.emailclient;

public class EmailClientPass implements IEmailSender<EmailNotification> {



    @Override
    public boolean send(final EmailNotification emailNotification) {

      return true;

    }

}
