package org.emailclient.policies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class RetryPolicy {

    private static final Logger logger = LoggerFactory.getLogger(RetryPolicy.class);

    private final TimeUnit timeUnit;

    private final long delay;

    private final int maxRetries;
    private final List<Class<? extends Exception>> handle;

    public RetryPolicy(TimeUnit timeUnit, long delay, int maxRetries, List<Class<? extends Exception>> handle) {
        this.timeUnit = timeUnit;
        this.delay = delay;
        this.maxRetries = maxRetries;
        this.handle = handle;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getDelay() {
        return delay;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public List<Class<? extends Exception>> getHandle() {
        return handle;
    }


    public static <T, R> R retry(RetryPolicy retryPolicy, Function<T, R> f, T t, Exception ex) {
        handle(retryPolicy, ex);


        if (retryPolicy.getMaxRetries() == 0) {
            return retryForEver(retryPolicy, f, t);
        }

        return retryMaxTries(retryPolicy, f, t);

    }

    private static <T, R> R retryMaxTries(RetryPolicy retryPolicy, Function<T, R> f, T t) {

        int tries = 0;
        while (tries < retryPolicy.getMaxRetries()) {

            //delay
            try {
                retryPolicy.getTimeUnit().sleep(retryPolicy.getDelay());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            logger.debug("retrying..");
            tries++;

            try {
                return f.apply(t);
            } catch (Exception ex2) {

                logger.error("Error in retry: ", ex2);
                handle(retryPolicy, ex2);

            }
        }

        throw new RuntimeException("Failure: Max retries reached...");
    }

    private static <T, R> R retryForEver(RetryPolicy retryPolicy, Function<T, R> f, T t) {
        while (true) {

            try {
                retryPolicy.getTimeUnit().sleep(retryPolicy.getDelay());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            logger.debug("retrying..");


            try {
                return f.apply(t);
            } catch (Exception ex2) {

                logger.error("Error in retry: ", ex2);
                handle(retryPolicy, ex2);

            }
        }

    }


    private static void handle(RetryPolicy retryPolicy, Exception ex) {

        if (retryPolicy.getHandle() == null || retryPolicy.getHandle().isEmpty()) {
            throw new RuntimeException("Policy enforcer could not handle exception", ex);
        }

        boolean found = retryPolicy.getHandle().stream().anyMatch(ex.getClass()::equals);

        if (!found) {
            throw new RuntimeException("Policy enforcer could not handle exception", ex);
        }

        logger.debug("Handling exception" + ex.getMessage());

    }

    public static RetryPolicyBuilder builder() {
        return new RetryPolicyBuilder();
    }


    public static class RetryPolicyBuilder {
        private TimeUnit timeUnit;

        private long delay;
        private int maxRetries;
        private List<Class<? extends Exception>> handle;

        public RetryPolicyBuilder withDelay(TimeUnit timeUnit, long delay) {
            this.timeUnit = timeUnit;
            this.delay = delay;
            return this;
        }

        public RetryPolicyBuilder withMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public RetryPolicyBuilder handle(List<Class<? extends Exception>> handle) {
            this.handle = handle;
            return this;
        }

        public RetryPolicy build() {

            if (maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries cannot be negative");
            }

            return new RetryPolicy(timeUnit, delay, maxRetries, handle);
        }
    }
}
