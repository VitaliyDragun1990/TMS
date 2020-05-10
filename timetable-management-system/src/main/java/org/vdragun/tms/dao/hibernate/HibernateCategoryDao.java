package org.vdragun.tms.dao.hibernate;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;

/**
 * Implementation of {@link CategoryDao} using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class HibernateCategoryDao extends BaseHibernateDao implements CategoryDao {

    public HibernateCategoryDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void save(Category category) {
        log.debug("Saving new category to the database: {}", category);
        execute(session -> session.save(category));
    }

    @Override
    public void saveAll(List<Category> categories) {
        log.debug("Saving {} new categories to the database", categories.size());
        int batchSize = getBatchSize();
        execute(session -> {
            for (int i = 0; i < categories.size(); i++) {
                // saves category instance into active session (but not in the database)
                session.persist(categories.get(i));
                if (i % batchSize == 0 || i == categories.size() - 1) {
                    // flush changes to the database (actually save categories into the database)
                    session.flush();
                    // clear session from all persisted entities
                    session.clear();
                }
            }
        });
    }

    @Override
    public Optional<Category> findById(Integer categoryId) {
        log.debug("Searching for category with id={} in the database", categoryId);
        return query(session -> Optional.ofNullable(session.get(Category.class, categoryId)));
    }

    @Override
    public List<Category> findAll() {
        log.debug("Retrieving all categories from the database");
        return query(session -> session.createQuery("SELECT c FROM Category c", Category.class).list());
    }

    @Override
    public boolean existsById(Integer categoryId) {
        log.debug("Checking whether category with id={} exists in the database", categoryId);
        return query(session -> {
            Query<Integer> query = session.createQuery(
                    "SELECT c.id FROM Category c WHERE c.id = ?1",
                    Integer.class);
            query.setParameter(1, categoryId);
            return !query.list().isEmpty();
        });
    }

}
