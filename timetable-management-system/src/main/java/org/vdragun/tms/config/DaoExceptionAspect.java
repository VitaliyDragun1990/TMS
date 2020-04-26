package org.vdragun.tms.config;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.vdragun.tms.dao.DaoException;

/**
 * Custom aspect to wrap Spring-specific {@link DataAccessException} exceptions
 * into application-specific {@link DaoException}
 * 
 * @author Vitaliy Dragun
 *
 */
@Aspect
public class DaoExceptionAspect {

    @AfterThrowing(pointcut = "execution(* org.vdragun.tms.dao.jdbc.*.*(..))", throwing = "ex")
    public void wrapDataAccessException(DataAccessException ex) {
        throw new DaoException(ex);
    }

}
