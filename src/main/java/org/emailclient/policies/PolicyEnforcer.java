package org.emailclient.policies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PolicyEnforcer<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEnforcer.class);

    private final List<IValidatePolicy<T>> validatePolicies;

    private final RetryPolicy retryPolicy;

    public PolicyEnforcer(List<IValidatePolicy<T>> validatePolicies, RetryPolicy retryPolicy) {
        this.validatePolicies = validatePolicies;
        this.retryPolicy = retryPolicy;
    }


    public R run(
            Function<T, R> f, T t
    ) {

        validate(t);

        try {
            return f.apply(t);
        } catch (Exception ex) {

            return retry(f, t, ex);

        }

    }

    public CompletableFuture<R> runAsync(
            Function<T, R> f, T t
    ) {

        return CompletableFuture.supplyAsync(() ->
                run(f, t)
        );

    }


    private void validate(final T t) {

        if (validatePolicies == null) {
            return;
        }

        for (IValidatePolicy<T> iValidatePolicy : validatePolicies) {

            try {

                if (!iValidatePolicy.validate(t)) {
                    break;
                }

            } catch (Exception ex) {

                if (iValidatePolicy.throwException()) {
                    throw ex;
                }

            }

        }

    }

    private R retry(Function<T, R> f, T t, Exception ex) {

        handle(ex);

        int tries = 0;
        int maxRetries = retryPolicy.getMaxRetries() == 0 ? 3 : retryPolicy.getMaxRetries();

        while (tries < maxRetries) {

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

                logger.error("Error in retry: ", ex);

            }
        }

        throw new RuntimeException("Failure: Max retries reached...");
    }

    private void handle(Exception ex) {

        //allow all exceptions
        if (retryPolicy.getHandle() == null || retryPolicy.getHandle().isEmpty()) {
            throw new RuntimeException("Policy enforcer could not handle exception", ex);
        }

        boolean found = retryPolicy.getHandle().stream().anyMatch(ex.getClass()::equals);

        if (!found) {
            throw new RuntimeException("Policy enforcer could not handle exception", ex);
        }

    }

}
