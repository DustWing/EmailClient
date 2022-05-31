package org.emailclient;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EmailClientQueueTest {

    final static String userName = "";
    final static String password = "";
    final static String fromEmail = "";
    final static List<String> toRecipients = List.of("");

    @Test
    void testQueue() {

        final Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        final EmailClient sender = EmailClient.create(props, userName, password);


        final EmailClientQueue queue = EmailClientQueue.create();

        try {

            queue.add(sender, createNotification("1"));

            Thread.sleep(3000);

            queue.pause();

            queue.add(sender, createNotification("2"));

            queue.add(sender, createNotification("3"));

            queue.resume();

            Thread.sleep(3000);

            queue.shutdown();

            assertTrue(true);

        } catch (Throwable ex) {
            fail(ex);
        }

    }

    private static EmailNotification createNotification(String subject) {
        return new EmailNotification.EmailNotificationBuilder()
                .setFromEmail(fromEmail)
                .setSubject("Test" + subject)
                .setBody("Test")
                .setToRecipients(toRecipients)
                .build();
    }

}