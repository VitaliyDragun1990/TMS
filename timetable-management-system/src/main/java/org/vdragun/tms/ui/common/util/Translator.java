package org.vdragun.tms.ui.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * Responsible for providing localized messages and formatting date values using
 * current locale
 * 
 * @author Vitaliy Dragun
 *
 */
public interface Translator {

    String getLocalizedMessage(String messageCode, Object... args);

    String formatDate(LocalDate date);

    String formatDateTime(LocalDateTime dateTime);

    String formatTimestamp(LocalDateTime timestamp);

    String formatMonth(Month month);
}
