package org.vdragun.tms.dao;

import org.vdragun.tms.core.application.exception.ApplicationException;

/**
 * Signals about error in DAO layer
 * 
 * @author Vitaliy Dragun
 *
 */
public class DaoException extends ApplicationException {
    private static final long serialVersionUID = -8996217905859013198L;

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

}
