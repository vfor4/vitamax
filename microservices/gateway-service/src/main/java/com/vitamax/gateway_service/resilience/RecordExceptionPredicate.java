package com.vitamax.gateway_service.resilience;

import java.util.function.Predicate;

public class RecordExceptionPredicate implements Predicate<Throwable> {
    @Override
    public boolean test(final Throwable throwable) {
        return true;
    }
}
