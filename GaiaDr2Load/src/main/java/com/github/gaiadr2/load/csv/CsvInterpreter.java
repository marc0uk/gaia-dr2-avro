package com.github.gaiadr2.load.csv;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Consumer;

/**
 * Intepreter for a line in a CSV file from the Gaia DR2
 */
public interface CsvInterpreter<F extends Enum<F>> extends Consumer<String> {

    /**
     * @param column A column expected to provide an {@code int} value
     * @return The value, when available
     */
    OptionalInt intValue(final F column);

    /**
     * @param column A column expected to provide a {@code long} value
     * @return The value, when available
     */
    OptionalLong longValue(final F column);

    /**
     * @param column A column expected to provide a {@code float} value
     * @return The value, when available
     */
    Optional<Float> floatValue(final F column);

    /**
     * @param column A column expected to provide a {@code double} value
     * @return The value, when available
     */
    OptionalDouble doubleValue(final F column);

    /**
     * @param column A column
     * @return The raw value of the column, when not empty
     */
    Optional<String> rawValue(final F column);
}
