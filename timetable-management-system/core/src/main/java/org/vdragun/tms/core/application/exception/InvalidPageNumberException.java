package org.vdragun.tms.core.application.exception;

/**
 * Signals that request page has invalid number.
 * 
 * @author Vitaliy Dragun
 *
 */
public class InvalidPageNumberException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1871879420088226549L;

    public InvalidPageNumberException(Class<?> resourceClass, int pageNumber, int pageSize, int lastPageNumber) {
        super(
                resourceClass,
                "Requested resource: [%s] - invalid page number:[%d], valid page numbers for current page size: [%d] are [0/1]-[%d/%d]",
                resourceClass.getSimpleName(), pageNumber, pageSize, lastPageNumber, lastPageNumber + 1);
    }

}
