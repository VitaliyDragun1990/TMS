package org.vdragun.tms.core.application.exception;

/**
 * Signals that request resource can not be found
 * 
 * @author Vitaliy Dragun
 *
 */
public class ResourceNotFoundException extends ApplicationException {
    private static final long serialVersionUID = 4930025280469096688L;

    private final Class<?> resourceClass;

    public ResourceNotFoundException(Class<?> resourceClass, String message, Object... args) {
        super(String.format(message, args));
        this.resourceClass = resourceClass;
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }

}
