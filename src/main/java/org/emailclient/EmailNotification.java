package org.emailclient;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class EmailNotification {

    private final String fromEmail;
    private final String subject;
    private final String body;
    private final boolean isHtml;
    private final Collection<EmailAttachment> attachments;
    private final Collection<String> toRecipients;
    private final Collection<String> ccRecipients;
    private final Collection<String> bccRecipients;

    private final Map<String, File> images;

    public String getFromEmail() {
        return fromEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Collection<EmailAttachment> getAttachments() {
        return attachments;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public Collection<String> getToRecipients() {
        return toRecipients;
    }

    public Collection<String> getCcRecipients() {
        return ccRecipients;
    }

    public Collection<String> getBccRecipients() {
        return bccRecipients;
    }

    public Map<String, File> getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "EmailNotification{" +
                "fromEmail=" + fromEmail +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", isHtml=" + isHtml +
                ", attachments=" + attachments +
                ", toRecipients=" + toRecipients +
                ", ccRecipients=" + ccRecipients +
                ", bccRecipients=" + bccRecipients +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailNotification that = (EmailNotification) o;
        return isHtml == that.isHtml && Objects.equals(fromEmail, that.fromEmail) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(body, that.body) &&
                Objects.equals(attachments, that.attachments) &&
                Objects.equals(toRecipients, that.toRecipients) &&
                Objects.equals(ccRecipients, that.ccRecipients) &&
                Objects.equals(bccRecipients, that.bccRecipients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromEmail, subject, body, isHtml, attachments, toRecipients, ccRecipients, bccRecipients);
    }


    public EmailNotification(
            String fromEmail,
            String subject,
            String body,
            Collection<String> toRecipients
    ) {
        this.fromEmail = fromEmail;
        this.subject = subject;
        this.body = body;
        this.isHtml = false;
        this.attachments = null;
        this.toRecipients = toRecipients;
        this.ccRecipients = null;
        this.bccRecipients = null;
        this.images = null;
    }

    public EmailNotification(
            String fromEmail,
            String subject,
            String body,
            boolean isHtml,
            Collection<EmailAttachment> attachments,
            Collection<String> toRecipients,
            Collection<String> ccRecipients,
            Collection<String> bccRecipients,
            Map<String, File> images) {
        this.fromEmail = fromEmail;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
        this.attachments = attachments;
        this.toRecipients = toRecipients;
        this.ccRecipients = ccRecipients;
        this.bccRecipients = bccRecipients;
        this.images = images;
    }


    public static class EmailNotificationBuilder {
        private String fromEmail;
        private String subject;
        private String body;
        private boolean isHtml;
        private Collection<EmailAttachment> attachments;
        private Collection<String> toRecipients;
        private Collection<String> ccRecipients;
        private Collection<String> bccRecipients;
        private Map<String, File> images;

        public EmailNotificationBuilder setFromEmail(String fromEmail) {
            this.fromEmail = fromEmail;
            return this;
        }

        public EmailNotificationBuilder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailNotificationBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        public EmailNotificationBuilder setIsHtml(boolean isHtml) {
            this.isHtml = isHtml;
            return this;
        }

        public EmailNotificationBuilder setAttachments(Collection<EmailAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public EmailNotificationBuilder setToRecipients(Collection<String> toRecipients) {
            this.toRecipients = toRecipients;
            return this;
        }

        public EmailNotificationBuilder setCcRecipients(Collection<String> ccRecipients) {
            this.ccRecipients = ccRecipients;
            return this;
        }

        public EmailNotificationBuilder setBccRecipients(Collection<String> bccRecipients) {
            this.bccRecipients = bccRecipients;
            return this;
        }

        public EmailNotificationBuilder setImages(Map<String, File> images) {
            this.images = images;
            return this;
        }

        public EmailNotification build() {

            if (fromEmail == null || fromEmail.isBlank()) {
                throw new IllegalArgumentException("fromEmail cannot be null or blank");
            }
            if (subject == null || subject.isBlank()) {
                throw new IllegalArgumentException("subject cannot be null or blank");
            }
            if (body == null || body.isBlank()) {
                throw new IllegalArgumentException("body cannot be null or blank");
            }
            if (toRecipients == null || toRecipients.isEmpty()) {
                throw new IllegalArgumentException("toRecipients cannot be null or empty");
            }

            return new EmailNotification(fromEmail, subject, body, isHtml, attachments, toRecipients, ccRecipients, bccRecipients, images);
        }
    }

}
