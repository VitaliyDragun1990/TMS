package org.vdragun.tms.core.application.exception;

/**
 * Parent class for all application-specific exceptions
 * 
 * @author Vitaliy Dragun
 *
 */
public abstract class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = -8089257786006101842L;

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

}
