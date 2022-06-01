package org.emailclient.policies;

import org.emailclient.EmailNotification;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spam policy can be shared in multiple IEmailSender
 */
public class SpamPolicy implements IValidatePolicy<EmailNotification> {

    private final Map<String, LocalDateTime> previousSubject;

    private final Duration coolDown;

    public SpamPolicy(Duration coolDown) {
        this.previousSubject = new ConcurrentHashMap<>();
        this.coolDown = coolDown;

    }

    @Override
    public boolean validate(EmailNotification notification) {

        //get the previous run
        final LocalDateTime previousRun = previousSubject.get(notification.getSubject());

        if (previousRun == null) {
            previousSubject.put(notification.getSubject(), LocalDateTime.now());
            return true;
        }

        //check if its still on cool down
        if(previousRun.isBefore(LocalDateTime.now().plus(coolDown))){
            throw new RuntimeException("Email Spamming " + notification);
        }

        //update
        previousSubject.put(notification.getSubject(), LocalDateTime.now());

        return true;
    }

    @Override
    public boolean throwException() {
        return true;
    }
}
