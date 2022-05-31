package org.emailclient;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class EmailClientTest {

    final static String userName = "";
    final static String password = "";
    final static String fromEmail = "";
    final static List<String> toRecipients = List.of("");
    final static List<String> ccRecipients = List.of("");
    final static List<String> bccRecipients = List.of("");


    @Test
    void testWithSsl() {

        EmailNotification notification = new EmailNotification.EmailNotificationBuilder()
                .setFromEmail(fromEmail)
                .setSubject("testWithSsl")
                .setBody("testWithSsl")
                .setIsHtml(true)
                .setToRecipients(toRecipients)
                .build();


        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        var client = EmailClient.create(props, userName, password);


        try {
            client.send(notification);
            assertTrue(true);
        } catch (EmailNotificationException ex) {
            ex.printStackTrace();
            fail();
        }


    }

    @Test
    void testWithCCAndBCC() {
        EmailNotification notification = new EmailNotification.EmailNotificationBuilder()
                .setFromEmail(fromEmail)
                .setSubject("testWithSsl")
                .setBody("testWithSsl")
                .setIsHtml(true)
                .setToRecipients(toRecipients)
                .setCcRecipients(ccRecipients)
                .setBccRecipients(bccRecipients)
                .build();


        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        var client = EmailClient.create(props, userName, password);


        try {
            client.send(notification);
            assertTrue(true);
        } catch (EmailNotificationException ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    void testAsync() {

        EmailNotification notification = new EmailNotification.EmailNotificationBuilder()
                .setFromEmail(fromEmail)
                .setSubject("testAsync")
                .setBody("testAsync")
                .setToRecipients(toRecipients)
                .build();


        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        var client = EmailClient.create(props, userName, password);

        try {
            client.sendAsync(notification).exceptionally((ex) -> {
                assertNull(ex);
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        }
    }

    @Test
    void testWithoutFromEmail() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailNotification.EmailNotificationBuilder()
                        .setSubject("testWithSsl")
                        .setBody("testWithSsl")
                        .setToRecipients(toRecipients)
                        .build()
        );
    }

    @Test
    void testWithoutSubject() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailNotification.EmailNotificationBuilder()
                        .setFromEmail(fromEmail)
                        .setBody("testWithSsl")
                        .setToRecipients(toRecipients)
                        .build()
        );
    }

    @Test
    void testWithoutBody() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailNotification.EmailNotificationBuilder()
                        .setFromEmail(fromEmail)
                        .setSubject("testWithSsl")
                        .setToRecipients(toRecipients)
                        .build()
        );
    }

    @Test
    void testWithoutRecipients() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailNotification.EmailNotificationBuilder()
                        .setFromEmail(fromEmail)
                        .setSubject("testWithSsl")
                        .setBody("testWithSsl")
                        .build()
        );
    }

    @Test
    void testWithTls() {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        EmailClient sender = EmailClient.create(props, userName, password);

        EmailNotification notification = new EmailNotification.EmailNotificationBuilder()
                .setFromEmail(fromEmail)
                .setSubject("testWithTls")
                .setBody("testWithTls")
                .setToRecipients(toRecipients)
                .build();

        try {
            sender.send(notification);
            assertTrue(true);
        } catch (EmailNotificationException ex) {
            fail();
        }
    }

    @Test
    void TestWithImages() {
        try {

            InputStream is = EmailClientTest.class.getClassLoader().getResourceAsStream("template/index.html");


            if (is == null) {
                throw new RuntimeException("no file found");
            }

            final String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);


            final String image1Path = "";
            final String image2Path = "";
            final String image3Path = "";
            final String image4Path = "";
            final String image5Path = "";


            final File image1 = new File(image1Path);
            final File image2 = new File(image2Path);
            final File image3 = new File(image3Path);
            final File image4 = new File(image4Path);
            final File image5 = new File(image5Path);

            Map<String, File> imageMap = new HashMap<>();

            imageMap.put("image1", image1);
            imageMap.put("image2", image2);
            imageMap.put("image3", image3);
            imageMap.put("image4", image4);
            imageMap.put("image5", image5);

            EmailNotification notification = new EmailNotification.EmailNotificationBuilder()
                    .setFromEmail(fromEmail)
                    .setSubject("testWithSsl")
                    .setBody(html)
                    .setIsHtml(true)
                    .setToRecipients(toRecipients)
                    .setImages(imageMap)
                    .build();


            Properties props = new Properties();

            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            final EmailClient client = EmailClient.create(props, userName, password);


            client.send(notification);

            assertTrue(true);

        } catch (Exception ex) {
            fail(ex);
        }
    }

}