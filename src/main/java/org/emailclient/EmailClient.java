package org.emailclient;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class EmailClient implements IEmailSender<EmailNotification> {

    final Session session;

    public static EmailClient create(
            final Properties properties,
            final String user,
            final String password
    ) {


        final Session session = Session.getDefaultInstance(
                properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        return new EmailClient(session);
    }


    public static EmailClient create(
            final Properties properties
    ) {
        return new EmailClient(Session.getDefaultInstance(properties));
    }

    public EmailClient(
            final Session session
    ) {
        this.session = session;
    }


    @Override
    public boolean send(final EmailNotification emailNotification) {

        try {

            Message message = buildMessage(emailNotification, session);

            Transport.send(message);

        } catch (MessagingException | IOException e) {
            throw new EmailNotificationException(e);
        }

        return true;

    }

    private Message buildMessage(
            final EmailNotification emailNotification, final Session session
    ) throws MessagingException, IOException {
        final boolean isHtml = emailNotification.isHtml();
        final String body = emailNotification.getBody();

        final MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(emailNotification.getFromEmail()));


        setRecipients(message, emailNotification.getToRecipients(), Message.RecipientType.TO);

        setRecipients(message, emailNotification.getCcRecipients(), Message.RecipientType.CC);

        setRecipients(message, emailNotification.getBccRecipients(), Message.RecipientType.BCC);

        message.setSubject(emailNotification.getSubject(), "UTF-8");


        //With Attachments and images
        if (emailNotification.getAttachments() != null && !emailNotification.getAttachments().isEmpty()) {

            withAttachments(emailNotification, isHtml, body, message);
        }
        //With images only
        else if (emailNotification.getImages() != null && !emailNotification.getImages().isEmpty()) {

            withHtmlInlineImages(emailNotification, isHtml, body, message);

        } else {
            if (isHtml) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body);
            }
        }
        return message;
    }

    private void withHtmlInlineImages(EmailNotification emailNotification, boolean isHtml, String body, MimeMessage message) throws MessagingException, IOException {
        if (!isHtml) {
            throw new EmailNotificationException("Cannot create email with images without html body. Set isHtml = true.");
        }

        // Create a multipart message
        final Multipart multipart = new MimeMultipart();

        // Create the message part
        final BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(body, "text/html; charset=utf-8");

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        addImagesInBody(multipart, emailNotification.getImages());

        // Send the complete message parts
        message.setContent(multipart);
    }

    private void withAttachments(EmailNotification emailNotification, boolean isHtml, String body, MimeMessage message) throws MessagingException, IOException {
        // Create a multipart message
        final Multipart multipart = new MimeMultipart();

        // Create the message part
        final BodyPart messageBodyPart = new MimeBodyPart();

        // Now set the actual message
        if (isHtml) {
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
            addImagesInBody(multipart, emailNotification.getImages());

        } else {
            messageBodyPart.setText(body);
        }

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        addAttachments(multipart, emailNotification.getAttachments());

        // Send the complete message parts
        message.setContent(multipart);
    }


    private void setRecipients(final MimeMessage message, Collection<String> recipients, Message.RecipientType type) {

        if (recipients == null) {
            return;
        }

        recipients.forEach(
                e -> {
                    try {
                        message.addRecipient(type, new InternetAddress(e));
                    } catch (MessagingException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );
    }

    private void addImagesInBody(final Multipart multipart, final Map<String, File> mapInlineImages) throws MessagingException, IOException {
        // adds inline image attachments

        if (mapInlineImages == null || mapInlineImages.isEmpty()) {
            return;
        }

        Set<String> setImageID = mapInlineImages.keySet();

        for (String contentId : setImageID) {
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setHeader("Content-ID", "<" + contentId + ">");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            imagePart.attachFile(mapInlineImages.get(contentId));
            multipart.addBodyPart(imagePart);
        }

    }

    private void addAttachments(final Multipart multipart, Collection<EmailAttachment> attachments) throws MessagingException {

        for (EmailAttachment att : attachments) {
            // Part two is attachment
            final BodyPart attachmentBodyPart = new MimeBodyPart();
            final DataSource source = new ByteArrayDataSource(att.getContent(), att.getMimeType());

            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(att.getFileName());

            multipart.addBodyPart(attachmentBodyPart);
        }
    }

}
