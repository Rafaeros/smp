package br.rafaeros.smp.core.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    private static final ZoneId ZONE_BR = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FMT_BR_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_BR_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private DateUtils() {}

    /**
     * Convert Instant to "dd/MM/yyyy" in Brazil timezone.
     */
    public static String toBRDate(Instant date) {
        if (date == null) return null;
        return FMT_BR_DATE.format(date.atZone(ZONE_BR));
    }

    /**
     * Convert Instant to "dd/MM/yyyy HH:mm:ss" in Brazil timezone.
     */
    public static String toBRDateTime(Instant date) {
        if (date == null) return null;
        return FMT_BR_DATETIME.format(date.atZone(ZONE_BR));
    }

    /**
     * Convert Instant to ISO-8601 standard (ex: "2026-02-04T10:15:30Z").
     */
    public static String toISO(Instant date) {
        if (date == null) return null;
        return date.toString();
    }

    /**
     * Convert "dd/MM/yyyy" to Instant
     */
    public static Instant parseBRDate(String dateStr) {
        if (isEmpty(dateStr)) return null;
        try {
            LocalDate ld = LocalDate.parse(dateStr, FMT_BR_DATE);
            return ld.atStartOfDay(ZONE_BR).toInstant();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato dd/MM/yyyy", e);
        }
    }

    /**
     * Convert "dd/MM/yyyy HH:mm:ss" to Instant.
     */
    public static Instant parseBRDateTime(String dateStr) {
        if (isEmpty(dateStr)) return null;
        try {
            LocalDateTime ldt = LocalDateTime.parse(dateStr, FMT_BR_DATETIME);
            return ldt.atZone(ZONE_BR).toInstant();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data/Hora inválida. Use o formato dd/MM/yyyy HH:mm:ss", e);
        }
    }

    /**
     * Convert ISO-8601 (ex: "2026-02-04T10:00:00Z") to Instant.
     */
    public static Instant parseISO(String isoStr) {
        if (isEmpty(isoStr)) return null;
        try {
            return Instant.parse(isoStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato ISO inválido.", e);
        }
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}