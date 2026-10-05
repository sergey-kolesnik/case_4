package com.example.meetingnotes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Формат даты и времени для списка и поиска. */
public final class DateFormatter {
    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("dd.MM.yyyy, HH:mm", new Locale("ru", "RU"));

    private DateFormatter() { }

    public static synchronized String format(long timestamp) {
        return FORMAT.format(new Date(timestamp));
    }
}
