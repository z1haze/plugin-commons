package me.z1haze.commons.update;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Versions {
    private static final Pattern VERSION = Pattern.compile("(\\d{1,18}(?:\\.\\d{1,18})*)(?:-(\\S+))?");

    private Versions() {
    }

    // equal numbers with two different suffixes can't be ordered, so they count as not newer
    public static boolean isNewer(String latest, String current) {
        Optional<Version> l = Version.parse(latest);
        Optional<Version> c = Version.parse(current);
        if (l.isEmpty() || c.isEmpty()) return false;

        int numbers = compareNumbers(l.get().numbers(), c.get().numbers());
        if (numbers != 0) return numbers > 0;

        return l.get().suffix() == null && c.get().suffix() != null;
    }

    private static int compareNumbers(long[] a, long[] b) {
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int cmp = Long.compare(i < a.length ? a[i] : 0, i < b.length ? b[i] : 0);
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    private record Version(long[] numbers, String suffix) {
        static Optional<Version> parse(String text) {
            if (text == null) return Optional.empty();

            Matcher matcher = VERSION.matcher(text.trim());
            if (!matcher.matches()) return Optional.empty();

            long[] numbers = Arrays.stream(matcher.group(1).split("\\.")).mapToLong(Long::parseLong).toArray();
            return Optional.of(new Version(numbers, matcher.group(2)));
        }
    }
}
