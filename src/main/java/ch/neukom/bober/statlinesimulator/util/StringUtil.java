package ch.neukom.bober.statlinesimulator.util;

import com.google.common.base.Strings;

public class StringUtil {
    private StringUtil() {}

    public static boolean isNotNullOrEmpty(String string) {
        return !Strings.isNullOrEmpty(string);
    }
}
