package me.z1haze.commons.update;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class VersionsTest {

    @ParameterizedTest(name = "{0} newer than {1}: {2}")
    @CsvSource({
            "2.0.1, 2.0.0, true",
            "2.0.0, 2.0.1, false",
            "2.0.10, 2.0.9, true",
            "2.0.9, 2.0.10, false",
            "3.0.0, 2.9.9, true",
            "2.0.0, 2.0.0, false",
    })
    void comparesNumbersPartByPart(String latest, String current, boolean newer) {
        assertEquals(newer, Versions.isNewer(latest, current));
    }

    @ParameterizedTest(name = "{0} newer than {1}: {2}")
    @CsvSource({
            "2.0.1, 2.0, true",
            "2.0, 2.0.1, false",
            "2.0, 2.0.0, false",
            "2.0.0, 2, false",
            "2.1, 2.0.5, true",
    })
    void treatsMissingPartsAsZero(String latest, String current, boolean newer) {
        assertEquals(newer, Versions.isNewer(latest, current));
    }

    @ParameterizedTest(name = "{0} newer than {1}: {2}")
    @CsvSource({
            "2.0.0, 2.0.0-beta, true",
            "2.0.0-beta, 2.0.0, false",
            "2.0.1-beta, 2.0.0, true",
            "2.0.0, 2.0.1-beta, false",
            "2.0.0-beta, 2.0.0-beta, false",
            "2.0.0-rc, 2.0.0-beta, false",
            "2.0.0-beta, 2.0.0-rc, false",
    })
    void ordersSuffixesBeforeTheRelease(String latest, String current, boolean newer) {
        assertEquals(newer, Versions.isNewer(latest, current));
    }

    @ParameterizedTest(name = "\"{0}\" newer than \"{1}\": false")
    @CsvSource({
            "'', 2.0.0",
            "2.0.0, ''",
            "abc, 2.0.0",
            "3.0.0, abc",
            "3..0, 2.0.0",
            "3.0.0., 2.0.0",
            "v3.0.0, 2.0.0",
            "3.0.0-, 2.0.0",
            "3.x.0, 2.0.0",
            "<html>, 2.0.0",
    })
    void unparsableIsNeverNewer(String latest, String current) {
        assertFalse(Versions.isNewer(latest, current));
    }
}
