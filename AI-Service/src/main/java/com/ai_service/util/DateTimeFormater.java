package com.ai_service.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for converting AI-generated date strings
 * into Java date-time objects.
 * <p>
 * - AI receives dates in human-readable format.
 * - Backend services need structured LocalDate/LocalDateTime objects.
 * <p>
 * - Takes date string from AI tool.
 * - Applies the required date format.
 * - Converts String into Java date-time object.
 * <p>
 * Example:
 * "10 August 2026 05:00 PM" → LocalDateTime
 */
public class DateTimeFormater {


    /**
     * Converts date and time string into LocalDateTime.
     * Used for operations where date and time are required,
     * such as booking an appointment.
     */
    public static LocalDateTime parseDateTime(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm a");
        return LocalDateTime.parse(value, formatter);
    }

    /**
     * Converts date string into LocalDate.
     * Used for operations where only date is required,
     * such as finding available appointment slots.
     */
    public static LocalDate parseDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return LocalDate.parse(value, formatter);
    }
}
