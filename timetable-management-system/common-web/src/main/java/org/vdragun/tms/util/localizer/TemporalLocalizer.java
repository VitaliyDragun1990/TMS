package org.vdragun.tms.util.localizer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * Responsible for providing localized string representation for specified
 * temporal value
 * 
 * @author Vitaliy Dragun
 *
 */
public interface TemporalLocalizer {

    String localizeDate(LocalDate date);

    String localizeDateDefault(LocalDate date);

    String localizeDateTime(LocalDateTime dateTime);

    String localizeDateTimeDefault(LocalDateTime dateTime);

    String localizeTimestamp(LocalDateTime timestamp);

    String localizeTimestampDefault(LocalDateTime timestamp);

    String localizeMonth(Month month);

}
