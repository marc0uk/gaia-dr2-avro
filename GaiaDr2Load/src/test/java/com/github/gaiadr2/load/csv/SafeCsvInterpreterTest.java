package com.github.gaiadr2.load.csv;

import com.github.gaiadr2.load.exception.NonCompliantColumnFailure;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Random;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link SafeCsvInterpreter}
 */
class SafeCsvInterpreterTest {

    enum Columns {
        IntColumn,
        LongColumn,
        StringColumn,
        FloatColumn,
        DoubleColumn
    }

    private final Random rng = new Random(424242242424L);
    private final CsvInterpreter<Columns> interpreter = new SafeCsvInterpreter<>(Columns.class);

    @ParameterizedTest
    @ValueSource(strings = {"int", "long", "float", "double", "raw"})
    void nonInitialisedInterpreterCausesException(final String type) {
        final InvocationTargetException ex = assertThrows(InvocationTargetException.class, () -> method(type).invoke(interpreter, Columns.IntColumn));
        assertThrows(IllegalArgumentException.class, () -> {
            throw ex.getCause();
        });
    }

    @Test
    void emptyIntColumnProducesEmptyOptional() {
        interpreter.accept(",12345,NotAvailable,1.345678,1987.6767");
        assertFalse(interpreter.intValue(Columns.IntColumn).isPresent());
        assertFalse(interpreter.rawValue(Columns.IntColumn).isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345,,NotAvailable,1.345678,1987.6767", ",,NotAvailable,1.345678,1987.6767"})
    void emptyLongColumnProducesEmptyOptional(final String line) {
        interpreter.accept(line);
        assertFalse(interpreter.longValue(Columns.LongColumn).isPresent());
        assertFalse(interpreter.rawValue(Columns.LongColumn).isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345,12345678980,NotAvailable,,1987.67678089089", ",,,,1987.67678089089"})
    void emptyFloatColumnProducesEmptyOptional(final String line) {
        interpreter.accept(line);
        assertFalse(interpreter.floatValue(Columns.FloatColumn).isPresent());
        assertFalse(interpreter.rawValue(Columns.FloatColumn).isPresent());
    }

    @Test
    void emptyDoubleColumnProducesEmptyOptional() {
        interpreter.accept("12345,12345678980,NotAvailable,1987.6767,");
        assertFalse(interpreter.doubleValue(Columns.DoubleColumn).isPresent());
        assertFalse(interpreter.rawValue(Columns.DoubleColumn).isPresent());
    }

    @Test
    void allEmptyIsFine() {
        interpreter.accept(",,,,");
        assertFalse(interpreter.intValue(Columns.IntColumn).isPresent());
        assertFalse(interpreter.longValue(Columns.LongColumn).isPresent());
        assertFalse(interpreter.floatValue(Columns.FloatColumn).isPresent());
        assertFalse(interpreter.doubleValue(Columns.DoubleColumn).isPresent());
    }

    @Test
    void tooFewColumnsCausesFailure() {
        assertThrows(
                IllegalArgumentException.class,
                () -> interpreter.accept("12345,12345678980,NotAvailable,1987.6767"));
    }

    @Test
    void tooManyColumnsCausesFailure() {
        assertThrows(
                IllegalArgumentException.class,
                () -> interpreter.accept("12345,12345678980,NotAvailable1987.6767,,1987.67678089089,23,12345,97"));
    }

    @Test
    void illegalIntegerValueCausesFailure() {
        interpreter.accept("Invalid,12345678890,Whatever,,");
        assertThrows(NonCompliantColumnFailure.class, () -> interpreter.intValue(Columns.IntColumn));
    }

    @Test
    void illegalLongValueCausesFailure() {
        interpreter.accept(",1.1,Whatever,,");
        assertThrows(NonCompliantColumnFailure.class, () -> interpreter.longValue(Columns.LongColumn));
    }

    @Test
    void illegalFloatValueCausesFailure() {
        interpreter.accept(",1,Whatever,1L,");
        assertThrows(NonCompliantColumnFailure.class, () -> interpreter.floatValue(Columns.FloatColumn));
    }

    @Test
    void illegalDoubleValueCausesFailure() {
        interpreter.accept(",1,Whatever,1.1,1L");
        assertThrows(NonCompliantColumnFailure.class, () -> interpreter.doubleValue(Columns.DoubleColumn));
    }

    @RepeatedTest(50)
    void validIntegerIsCorrectlyInterpreted() {
        final int expected = rng.nextInt();
        interpreter.accept(format("%d,1L,Whatever,1f,1D", expected));
        final OptionalInt optVal = interpreter.intValue(Columns.IntColumn);
        assertTrue(optVal.isPresent());
        assertEquals(expected, optVal.getAsInt());
    }

    @RepeatedTest(50)
    void validLongIsCorrectlyInterpreted() {
        final long expected = rng.nextLong();
        interpreter.accept(format("1,%d,Whatever,1f,1D", expected));
        final OptionalLong optVal = interpreter.longValue(Columns.LongColumn);
        assertTrue(optVal.isPresent());
        assertEquals(expected, optVal.getAsLong());
    }

    @RepeatedTest(50)
    void validFloatIsCorrectlyInterpreted() {
        final float expected = rng.nextFloat();
        interpreter.accept(format("1,1L,Whatever,%s,1D", Float.toString(expected)));
        final Optional<Float> optVal = interpreter.floatValue(Columns.FloatColumn);
        assertTrue(optVal.isPresent());
        assertEquals(expected, optVal.get(), 1E-3);
    }

    @RepeatedTest(50)
    void validDoubleIsCorrectlyInterpreted() {
        final double expected = rng.nextDouble();
        interpreter.accept(format("1,1L,Whatever,1f,%s", Double.toString(expected)));
        final OptionalDouble optVal = interpreter.doubleValue(Columns.DoubleColumn);
        assertTrue(optVal.isPresent());
        assertEquals(expected, optVal.getAsDouble(), 1E-30);
    }

    @Test
    void populatedColumnReturnsFilledOptional() {
        final String expected = "Valid";
        interpreter.accept(format("12345,12345678980,%s,1987.6767,", expected));
        final Optional<String> optVal = interpreter.rawValue(Columns.StringColumn);
        assertTrue(optVal.isPresent());
        assertEquals(expected, optVal.get());
    }

    private Method method(final String type) throws NoSuchMethodException {
        return SafeCsvInterpreter.class.getMethod(format("%sValue", type), Enum.class);
    }

}
