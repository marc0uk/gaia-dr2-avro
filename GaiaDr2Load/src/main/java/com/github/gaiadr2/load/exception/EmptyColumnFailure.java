package com.github.gaiadr2.load.exception;

import static java.lang.String.format;

/**
 *
 */
public final class EmptyColumnFailure extends RuntimeException {

    private EmptyColumnFailure(final String message) {
        super(message);
    }

    public static <C extends Enum<C>> EmptyColumnFailure emptyColumn(final C column) {
        return new EmptyColumnFailure(format("Required column %d [%s] is empty", column.ordinal(), column.name()));
    }
}
