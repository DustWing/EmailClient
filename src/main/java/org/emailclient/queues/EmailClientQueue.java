package org.emailclient.queues;

import org.emailclient.EmailNotification;
import org.emailclient.INotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmailClientQueue implements IEmailClientQueue<EmailNotification> {

    private final static Logger sLogger = LoggerFactory.getLogger(EmailClientQueue.class);

    private final BlockingQueue<QueueItem> queue;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static EmailClientQueue create() {

        return new EmailClientQueue(
                new LinkedBlockingQueue<>()
        );
    }

    public EmailClientQueue(
            BlockingQueue<QueueItem> queue
    ) {
        this.queue = queue;

    }

    @Override
    public void add(
            INotificationSender<EmailNotification> emailSender, EmailNotification notification
    ) {

        this.queue.add(
                new QueueItem(
                        emailSender,
                        notification
                )
        );
    }

    @Override
    public void start() {

        executorService.submit(
                () -> {
                    while (!Thread.currentThread().isInterrupted()) {

                        if (!isRunning.get()) {
                            continue;
                        }
                        try {

                            final QueueItem item = queue.take();

                            item.getEmailSender().send(item.getNotification());

                        } catch (InterruptedException e) {
                            sLogger.error("Queue interrupted", e);
                            break;
                        } catch (Throwable ex) {
                            sLogger.error("Exception in Email queue: ", ex);
                        }

                    }
                }
        );
    }

    @Override
    public void resume() {
        isRunning.set(true);
    }

    @Override
    public void pause() {
        isRunning.set(false);
    }

    @Override
    public void shutdown() {
        sLogger.debug("Shutting down....");
        executorService.shutdown();
    }


    private static class QueueItem {

        private final INotificationSender<EmailNotification> emailSender;
        private final EmailNotification notification;


        private QueueItem(INotificationSender<EmailNotification> emailSender, EmailNotification notification) {
            this.emailSender = emailSender;
            this.notification = notification;
        }

        public INotificationSender<EmailNotification> getEmailSender() {
            return emailSender;
        }

        public EmailNotification getNotification() {
            return notification;
        }
    }


}
