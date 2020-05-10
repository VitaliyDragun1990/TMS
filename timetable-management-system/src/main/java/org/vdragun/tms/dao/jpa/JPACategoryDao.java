package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;

/**
 * Implementation of {@link CategoryDao} using JPA
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JPACategoryDao extends BaseJPADao implements CategoryDao {

    public JPACategoryDao(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    public void save(Category category) {
        log.debug("Saving new category to the database: {}", category);
        execute(entityManager -> entityManager.persist(category));
    }

    @Override
    public void saveAll(List<Category> categories) {
        log.debug("Saving {} new categories to the database", categories.size());
        int batchSize = getBatchSize();
        execute(entityManager -> {
            for (int i = 0; i < categories.size(); i++) {
                // saves category instance into active persistent context (but not in the database)
                entityManager.persist(categories.get(i));
                if (i % batchSize == 0 || i == categories.size() - 1) {
                    // flush changes to the database (actually save categories into the database)
                    entityManager.flush();
                    // clear persistent context from all persisted entities
                    entityManager.clear();
                }
            }
        });
    }

    @Override
    public Optional<Category> findById(Integer categoryId) {
        log.debug("Searching for category with id={} in the database", categoryId);
        return query(entityManager -> Optional.ofNullable(entityManager.find(Category.class, categoryId)));
    }

    @Override
    public List<Category> findAll() {
        log.debug("Retrieving all categories from the database");
        return query(entityManager -> entityManager.createQuery("SELECT c FROM Category c", Category.class)
                .getResultList());
    }

    @Override
    public boolean existsById(Integer categoryId) {
        log.debug("Checking whether category with id={} exists in the database", categoryId);
        return query(entityManager -> {
            TypedQuery<Integer> query = entityManager.createQuery(
                    "SELECT c.id FROM Category c WHERE c.id = ?1",
                    Integer.class);
            query.setParameter(1, categoryId);
            return !query.getResultList().isEmpty();
        });
    }

}
