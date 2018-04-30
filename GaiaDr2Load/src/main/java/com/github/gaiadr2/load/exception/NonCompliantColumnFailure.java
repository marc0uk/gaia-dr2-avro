package com.github.gaiadr2.load.exception;

import static java.lang.String.format;

/**
 * This failure mode happens when the value for a CSV file column does not match the type
 * specified in the Gaia DR2 archive data model.
 *
 * @author Marco Riello
 */
public final class NonCompliantColumnFailure extends RuntimeException {

    private NonCompliantColumnFailure(final String message) {
        super(message);
    }

    /**
     * @param column Column
     * @param value Value of the column
     * @return Exception for the value not being a valid integer
     */
    public static <C extends Enum<C>> NonCompliantColumnFailure notInt(final C column, final String value) {
        return new NonCompliantColumnFailure(message("int", column, value));
    }

    /**
     * @param column Column
     * @param value Value of the column
     * @return Exception for the value not being a valid long integer
     */
    public static <C extends Enum<C>> NonCompliantColumnFailure notLong(final C column, final String value) {
        return new NonCompliantColumnFailure(message("long", column, value));
    }

    /**
     * @param column Column
     * @param value Value of the column
     * @return Exception for the value not being a valid float value
     */
    public static <C extends Enum<C>> NonCompliantColumnFailure notFloat(final C column, final String value) {
        return new NonCompliantColumnFailure(message("float", column, value));
    }

    /**
     * @param column Column
     * @param value Value of the column
     * @return Exception for the value not being a valid double value
     */
    public static <C extends Enum<C>> NonCompliantColumnFailure notDouble(final C column, final String value) {
        return new NonCompliantColumnFailure(message("double", column, value));
    }

    /**
     * Format the error message
     *
     * @param type Name of the expected type
     * @param column Column
     * @param value Value of the column
     * @return Error message to use in the exception
     */
    private static <C extends Enum<C>> String message(final String type, final C column, final String value) {
        return format(
                "Column %d [%s] should have been %s, but was: %s",
                column.ordinal(),
                column.name(),
                type,
                value);
    }
}
