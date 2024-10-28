package ch.neukom.bober.statlinesimulator.util;

import com.google.common.base.Strings;

public class StringUtil {
    private StringUtil() {}

    public static boolean isNotNullOrEmpty(String string) {
        return !Strings.isNullOrEmpty(string);
    }

    public static String withFirstLetterUpperCase(String string) {
        if (string.length() <= 1) {
            return string.toUpperCase();
        }
        return Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase();
    }
}
