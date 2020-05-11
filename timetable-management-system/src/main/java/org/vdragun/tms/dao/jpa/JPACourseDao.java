package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.dao.CourseDao;

/**
 * Implementation of {@link CourseDao} using JPA
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JPACourseDao implements CourseDao {

    private static final Logger LOG = LoggerFactory.getLogger(JPACourseDao.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize;

    @Override
    public void save(Course course) {
        LOG.debug("Saving new course to the database: {}", course);
        entityManager.persist(course);
    }

    @Override
    public void saveAll(List<Course> courses) {
        LOG.debug("Saving {} new courses to the database", courses.size());
        for (int i = 0; i < courses.size(); i++) {
            // saves course instance into active persistent context (but not in the database)
            entityManager.persist(courses.get(i));
            if (i % batchSize == 0 || i == courses.size() - 1) {
                // flush changes to the database (actually save courses into the database)
                entityManager.flush();
                // clear persistent context from all persisted entities
                entityManager.clear();
            }
        }
    }

    @Override
    public Optional<Course> findById(Integer courseId) {
        LOG.debug("Searching for course with id={} in the database", courseId);
        return Optional.ofNullable(entityManager.find(Course.class, courseId));
    }

    @Override
    public List<Course> findAll() {
        LOG.debug("Retrieving all courses from the database");
        return entityManager.createQuery("SELECT c FROM Course c", Course.class).getResultList();
    }

    @Override
    public List<Course> findByCategory(Integer categoryId) {
        LOG.debug("Retrieving all courses belonging to category with id={} from the database", categoryId);
        TypedQuery<Course> query = entityManager.createQuery("SELECT c FROM Course c WHERE c.category.id = ?1",
                Course.class);
        query.setParameter(1, categoryId);
        return query.getResultList();
    }

    @Override
    public boolean existsById(Integer courseId) {
        LOG.debug("Checking whether course with id={} exists in the database", courseId);
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT c.id FROM Course c WHERE c.id = ?1",
                Integer.class);
        query.setParameter(1, courseId);
        return !query.getResultList().isEmpty();
    }

}
