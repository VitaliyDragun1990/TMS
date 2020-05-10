package org.vdragun.tms.dao.jpa;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vdragun.tms.dao.DaoException;

/**
 * Base class for all JPA-based DAO implementations
 * 
 * @author Vitaliy Dragun
 *
 */
abstract class BaseJPADao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private EntityManagerFactory factory;

    protected BaseJPADao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    /**
     * Returns currently configured size of the batch insert/update operations
     */
    protected int getBatchSize() {
        return factory.unwrap(SessionFactory.class).getSessionFactoryOptions().getJdbcBatchSize();
    }

    /**
     * Executes any transaction manager-provided command inside database transaction
     * 
     * @param action command to execute
     */
    protected void execute(Consumer<EntityManager> action) {
        EntityTransaction transaction = null;
        EntityManager manager = null;
        try {
            manager = factory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            
            action.accept(manager);
            transaction.commit();
        } catch (Exception e) {
            handleExcetion(transaction, e);
            throw new DaoException(e);
        } finally {
            closeManager(manager);
        }
    }

    /**
     * Executes any entity manager-provided query and returns query result
     * 
     * @param func query to execute
     * @param <R>  type of the result to be returned
     */
    protected <R> R query(Function<EntityManager, R> func) {
        EntityManager manager = null;
        try {
            manager = factory.createEntityManager();

            return func.apply(manager);
        } catch (Exception e) {
            throw new DaoException(e);
        } finally {
            closeManager(manager);
        }
    }

    private void handleExcetion(EntityTransaction transaction, Exception exc) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception e) {
                exc.addSuppressed(e);
            }
        }
    }

    private void closeManager(EntityManager manager) {
        if (manager != null) {
            manager.close();
        }
    }
}
