package org.vdragun.tms.dao.hibernate;

import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vdragun.tms.dao.DaoException;

/**
 * Base class for all Hibernate-based DAO implementations
 * 
 * @author Vitaliy Dragun
 *
 */
abstract class BaseHibernateDao {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    protected BaseHibernateDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns currently configured size of the batch insert/update operations
     */
    protected int getBatchSize() {
        return sessionFactory.getSessionFactoryOptions().getJdbcBatchSize();
    }

    /**
     * Executes any session-provided command inside database transaction
     * 
     * @param action command to execute
     */
    protected void execute(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            handleException(transaction, e);
            throw new DaoException(e);
        }
    }

    private void handleException(Transaction transaction, Exception exc) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception e) {
                exc.addSuppressed(e);
            }
        }
    }

    /**
     * Executes any session-provided query and returns query result
     * 
     * @param func query to execute
     * @param <R>  type of the result to be returned
     */
    protected <R> R query(Function<Session, R> func) {
        try (Session session = sessionFactory.openSession()) {
            return func.apply(session);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

}
