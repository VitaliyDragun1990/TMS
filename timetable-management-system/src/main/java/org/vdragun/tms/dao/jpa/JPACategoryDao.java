package org.vdragun.tms.dao.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class JPACategoryDao implements CategoryDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(JPACategoryDao.class);

    @PersistenceContext
    private EntityManager entityManager;
    
    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize;
    
    @Override
    public void save(Category category) {
        LOG.debug("Saving new category to the database: {}", category);
        entityManager.persist(category);
    }

    @Override
    public void saveAll(Iterable<Category> categories) {
        List<Category> list = new ArrayList<>();
        categories.forEach(list::add);
        LOG.debug("Saving {} new categories to the database", list.size());
        for (int i = 0; i < list.size(); i++) {
            // saves category instance into active persistent context (but not in the database)
            entityManager.persist(list.get(i));
            if (i % batchSize == 0 || i == list.size() - 1) {
                // flush changes to the database (actually save categories into the database)
                entityManager.flush();
                // clear persistent context from all persisted entities
                entityManager.clear();
            }
        }
    }

    @Override
    public Optional<Category> findById(Integer categoryId) {
        LOG.debug("Searching for category with id={} in the database", categoryId);
        return Optional.ofNullable(entityManager.find(Category.class, categoryId));
    }

    @Override
    public List<Category> findAll() {
        LOG.debug("Retrieving all categories from the database");
        return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    @Override
    public boolean existsById(Integer categoryId) {
        LOG.debug("Checking whether category with id={} exists in the database", categoryId);
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT c.id FROM Category c WHERE c.id = ?1",
                Integer.class);
        query.setParameter(1, categoryId);
        return !query.getResultList().isEmpty();
    }

}
