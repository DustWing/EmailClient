package org.emailclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmailClientQueue {

    private final static Logger sLogger = LoggerFactory.getLogger(EmailClientQueue.class);

    private final BlockingQueue<QueueItem> queue;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static EmailClientQueue create() {

        final EmailClientQueue emailClientQueue = new EmailClientQueue(
                new LinkedBlockingQueue<>()
        );
        emailClientQueue.run();
        return emailClientQueue;
    }

    public EmailClientQueue(
            BlockingQueue<QueueItem> queue
    ) {
        this.queue = queue;
    }

    public void add(
            IEmailSender emailSender, EmailNotification notification
    ) {

        this.queue.add(
                new QueueItem(
                        emailSender,
                        notification
                )
        );
    }

    public void run() {

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

    public void resume() {
        isRunning.set(true);
    }

    public void pause() {
        isRunning.set(false);
    }

    public void shutdown() {
        sLogger.debug("Shutting down....");
        executorService.shutdown();
    }


    private static class QueueItem {

        private final IEmailSender emailSender;
        private final EmailNotification notification;


        private QueueItem(IEmailSender emailSender, EmailNotification notification) {
            this.emailSender = emailSender;
            this.notification = notification;
        }

        public IEmailSender getEmailSender() {
            return emailSender;
        }

        public EmailNotification getNotification() {
            return notification;
        }
    }


}
