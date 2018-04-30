package com.github.gaiadr2.load.csv;

import com.github.gaiadr2.load.exception.NonCompliantColumnFailure;

import java.io.Serializable;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static java.lang.String.format;

/**
 * A {@link CsvInterpreter} that will perform validation on the expected parameters, throwing exceptions
 * when not compliant.
 *
 * For each provided CSV line, the interpreter will ensure that the number of columns match the column
 * specification provided by the {@code <F>} enumeration. When issuing a typed request for the column
 * value, the inter
 */
public final class SafeCsvInterpreter<F extends Enum<F>> implements CsvInterpreter<F>, Serializable {

    private static final long serialVersionUID = -8114339944455167367L;

    /** Expected number of columns */
    private final int universeSize;

    /** Current set of column values */
    private transient String[] tokens = {};

    /**
     * @param columnType Enum representing the CSV columns in the expected order
     */
    public SafeCsvInterpreter(final Class<F> columnType) {
        universeSize = columnType.getEnumConstants().length;
    }

    @Override
    public OptionalInt intValue(final F column) {
        final String raw = raw(column);
        if (raw.isEmpty()) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(raw));
        } catch (NumberFormatException e) {
            throw NonCompliantColumnFailure.notInt(column, raw);
        }
    }

    @Override
    public OptionalLong longValue(final F column) {
        final String raw = raw(column);
        if (raw.isEmpty()) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong(raw));
        } catch (NumberFormatException e) {
            throw NonCompliantColumnFailure.notLong(column, raw);
        }
    }

    @Override
    public Optional<Float> floatValue(final F column) {
        final String raw = raw(column);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Float.parseFloat(raw));
        } catch (NumberFormatException e) {
            throw NonCompliantColumnFailure.notFloat(column, raw);
        }
    }

    @Override
    public OptionalDouble doubleValue(final F column) {
        final String raw = raw(column);
        if (raw.isEmpty()) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(raw));
        } catch (NumberFormatException e) {
            throw NonCompliantColumnFailure.notDouble(column, raw);
        }
    }

    @Override
    public Optional<Boolean> booleanValue(final F column) {
        final String raw = raw(column);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        switch (raw) {
            case "true":
                return Optional.of(true);
            case "false":
                return Optional.of(false);
            default:
                throw NonCompliantColumnFailure.notBoolean(column, raw);
        }
    }

    @Override
    public Optional<String> rawValue(final F column) {
        final String raw = raw(column);
        return raw.isEmpty() ?
                Optional.empty() :
                Optional.of(raw);
    }

    @Override
    public void accept(final String line) {
        final String[] parts = line.trim().split(",", -1);
        if (parts.length != universeSize) {
            tokens = new String[]{};
            throw new IllegalArgumentException(format(
                    "Expected %d columns in the CSV file but found %d",
                    universeSize,
                    parts.length));
        }
        tokens = parts;
    }

    private String raw(final F columnName) {
        try {
            return tokens[columnName.ordinal()].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(format(
                    "Expected %d columns in the CSV file but found %d",
                    universeSize,
                    tokens.length));
        }
    }
}
