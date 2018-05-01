package com.github.gaiadr2.load.interpreters;

import com.github.gaiadr2.load.csv.CsvInterpreter;
import com.github.gaiadr2.load.csv.SafeCsvInterpreter;

import static com.github.gaiadr2.load.exception.EmptyColumnFailure.emptyColumn;

/**
 * Common implementation for CSV interpreters providing basic utility methods
 */
abstract class AnyInterpreter<C extends Enum<C>> {

    protected final CsvInterpreter<C> interpreter;

    AnyInterpreter(final Class<C> columnSpec) {
        interpreter = new SafeCsvInterpreter<>(columnSpec);
    }

    /**
     * @param column A required column (cannot be empty)
     * @return the {@code int} column value
     */
    final int getInt(final C column) {
        return interpreter.intValue(column).orElseThrow(() -> emptyColumn(column));
    }

    /**
     * @param column A required column (cannot be empty)
     * @return the {@code long} column value
     */
    long getLong(final C column) {
        return interpreter.longValue(column).orElseThrow(() -> emptyColumn(column));
    }

    /**
     * @param column A required column (cannot be empty)
     * @return the {@code float} column value
     */
    float getFloat(final C column) {
        return interpreter.floatValue(column).orElseThrow(() -> emptyColumn(column));
    }

    /**
     * @param column A required column (cannot be empty)
     * @return the {@code double} column value
     */
    double getDouble(final C column) {
        return interpreter.doubleValue(column).orElseThrow(() -> emptyColumn(column));
    }

    /**
     * @param column A required column (cannot be empty
     * @return
     */
    boolean getBoolean(final C column) {
        return interpreter.booleanValue(column).orElseThrow(() -> emptyColumn(column));
    }

    String getRaw(final C column) {
        return interpreter.rawValue(column).orElseThrow(() -> emptyColumn(column));
    }
}
