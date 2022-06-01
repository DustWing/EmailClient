package org.emailclient.policies;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RetryPolicy {

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
            return new RetryPolicy(timeUnit,delay, maxRetries, handle);
        }
    }
}
