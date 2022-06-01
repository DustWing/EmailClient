package org.emailclient;

import org.emailclient.policies.PolicyEnforcer;
import org.emailclient.policies.RetryPolicy;
import org.emailclient.policies.SpamPolicy;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


public class PolicyEnforcerTest {


    @Test
    void test() {
        EmailNotification notification = new EmailNotification.EmailNotificationBuilder()
                .setFromEmail("fromEmail")
                .setSubject("PolicyEnforcerTest")
                .setBody("PolicyEnforcerTest")
                .setIsHtml(true)
                .setToRecipients(List.of(""))
                .build();


        var client = new EmailClientError();


        RetryPolicy retryPolicy = RetryPolicy.builder()
                .withDelay(TimeUnit.SECONDS, 1)
                .withMaxRetries(3)
                .handle(List.of(EmailNotificationException.class))
                .build();


        SpamPolicy spamPolicy = new SpamPolicy(Duration.ofSeconds(4));


        PolicyEnforcer<EmailNotification, Boolean> enforcer = new PolicyEnforcer<>(
                List.of(spamPolicy),
                retryPolicy
        );


        assertThrowsExactly(
                RuntimeException.class,
                () -> enforcer.run(
                        client::send, notification
                )
        );

    }

    @Test
    void testAsync() {
        EmailNotification notification = new EmailNotification.EmailNotificationBuilder()
                .setFromEmail("fromEmail")
                .setSubject("PolicyEnforcerTestAsync")
                .setBody("PolicyEnforcerTestAsync")
                .setIsHtml(true)
                .setToRecipients(List.of(""))
                .build();


        var client = new EmailClientPass();


        RetryPolicy retryPolicy = RetryPolicy.builder()
                .withDelay(TimeUnit.SECONDS, 1)
                .withMaxRetries(3)
                .handle(List.of(EmailNotificationException.class))
                .build();


        SpamPolicy spamPolicy = new SpamPolicy(Duration.ofSeconds(4));


        PolicyEnforcer<EmailNotification, Boolean> enforcer = new PolicyEnforcer<>(
                List.of(spamPolicy),
                retryPolicy
        );


        try {

            assertTrue(
                    enforcer.runAsync(client::send, notification)
                            .get()
            );

        } catch (Exception e) {
            fail(e);
        }

    }

}
