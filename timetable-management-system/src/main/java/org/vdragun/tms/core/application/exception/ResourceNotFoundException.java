package org.vdragun.tms.core.application.exception;

/**
 * Signals that request resource can not be found
 * 
 * @author Vitaliy Dragun
 *
 */
public class ResourceNotFoundException extends ApplicationException {
    private static final long serialVersionUID = 4930025280469096688L;

    public ResourceNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }

}
