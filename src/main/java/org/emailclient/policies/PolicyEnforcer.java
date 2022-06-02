package org.emailclient.policies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.emailclient.policies.RetryPolicy.retry;

public class PolicyEnforcer<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEnforcer.class);

    private final List<IValidatePolicy<T>> validatePolicies;

    private final RetryPolicy retryPolicy;

    private final List<Function<T, R>> fallBack;

    public PolicyEnforcer(
            List<IValidatePolicy<T>> validatePolicies,
            RetryPolicy retryPolicy,
            List<Function<T, R>> fallBack
    ) {
        this.validatePolicies = validatePolicies;
        this.retryPolicy = retryPolicy;
        this.fallBack = fallBack;
    }


    public R run(
            Function<T, R> f, T t
    ) {

        validate(t);

        try {

            return f.apply(t);

        } catch (Exception ex) {

            if (retryPolicy == null) {
                throw ex;
            }

            try {
                return retry(retryPolicy, f, t, ex);
            } catch (Exception ex2) {

                if (fallBack != null)
                    return runFallback(t);

                throw ex2;
            }
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

    private R runFallback(T t) {


        logger.info("Starting fallback ...");

        for (Function<T, R> trFunction : fallBack) {

            try {
                return trFunction.apply(t);
            } catch (Exception ex) {
                logger.error("Fall Back failure index[{}]", fallBack.indexOf(trFunction));
            }

        }

        throw new RuntimeException("Fallback failed...");
    }

    public static <T, R> PolicyEnforcerBuilder<T, R> builder() {
        return new PolicyEnforcerBuilder<>();
    }

    public static class PolicyEnforcerBuilder<T, R> {
        private List<IValidatePolicy<T>> validatePolicies;
        private RetryPolicy retryPolicy;
        private List<Function<T, R>> fallBack;

        public PolicyEnforcerBuilder<T, R> withValidations(List<IValidatePolicy<T>> validatePolicies) {
            this.validatePolicies = validatePolicies;
            return this;
        }

        public PolicyEnforcerBuilder<T, R> retry(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public PolicyEnforcerBuilder<T, R> setFallBack(List<Function<T, R>> fallBack) {
            this.fallBack = fallBack;
            return this;
        }

        public PolicyEnforcer<T, R> build() {
            return new PolicyEnforcer<>(validatePolicies, retryPolicy, fallBack);
        }
    }


}
