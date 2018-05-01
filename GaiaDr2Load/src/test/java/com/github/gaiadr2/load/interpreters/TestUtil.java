package com.github.gaiadr2.load.interpreters;

import com.github.gaiadr2.avro.common.Dval;
import com.github.gaiadr2.avro.source.AstroPar;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Base class for the interpreter unit tests
 */
final class TestUtil {

    /**
     * @param resource Path to the CSV resource to read
     * @return The lines read from the file
     */
    static List<String> streamResource(final String resource) {
        final URL url = TestUtil.class.getResource(resource);
        if (url == null) {
            throw new IllegalArgumentException("Could not load resource: " + resource);
        }
        try {
            final List<String> lines = Files.readAllLines(Paths.get(url.toURI()));
            lines.remove(0);
            return lines;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Could not parse resource");
        }
    }

    static void assertEquals(final double expVal, final double expErr, final Dval actual) {
        Assertions.assertEquals(expVal, actual.getValue(), 1E-30);
        Assertions.assertEquals(expErr, actual.getUncertainty(), 1E-30);
    }

    static void assertEquals(final float val, final float low, final float up, final AstroPar actual) {
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(val, actual.getValue(), 1E-8f);
        Assertions.assertEquals(low, actual.getPercentileLower(), 1E-8f);
        Assertions.assertEquals(up, actual.getPercentileUpper(), 1E-8f);
    }

    static void assertFloat(final float expected, final Float actual) {
        if (Float.isFinite(expected)) {
            Assertions.assertEquals(expected, actual, 1E-5f);
        } else {
            Assertions.assertNull(actual);
        }
    }
}
