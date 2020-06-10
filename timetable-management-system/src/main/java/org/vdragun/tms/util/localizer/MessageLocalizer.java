package org.vdragun.tms.util.localizer;

/**
 * Responsible for providing localized messages
 * 
 * @author Vitaliy Dragun
 *
 */
public interface MessageLocalizer {

    String getLocalizedMessage(String messageCode, Object... args);
}
