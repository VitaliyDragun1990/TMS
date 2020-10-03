package org.vdragun.tms.core.application.exception;

/**
 * Signals about error concerning application configuration
 *
 * @author Vitaliy Dragun
 */
public class ConfigurationException extends ApplicationException {
    private static final long serialVersionUID = -4762494677489266254L;

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }

}
