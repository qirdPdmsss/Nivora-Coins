package de.qirdpdms.nivoraCoins.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class NumberFormatter {

    private NumberFormatter() {
    }

    public static String format(long value, Locale locale) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
        numberFormat.setGroupingUsed(true);
        return numberFormat.format(value);
    }
}

