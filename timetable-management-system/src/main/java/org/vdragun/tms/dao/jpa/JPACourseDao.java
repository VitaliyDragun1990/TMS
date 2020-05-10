package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

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
public class JPACourseDao extends BaseJPADao implements CourseDao {

    public JPACourseDao(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    public void save(Course course) {
        log.debug("Saving new course to the database: {}", course);
        execute(entityManager -> entityManager.persist(course));
    }

    @Override
    public void saveAll(List<Course> courses) {
        log.debug("Saving {} new courses to the database", courses.size());
        int batchSize = getBatchSize();
        execute(ityManager -> {
            for (int i = 0; i < courses.size(); i++) {
                // saves course instance into active persistent context (but not in the database)
                ityManager.persist(courses.get(i));
                if (i % batchSize == 0 || i == courses.size() - 1) {
                    // flush changes to the database (actually save courses into the database)
                    ityManager.flush();
                    // clear persistent context from all persisted entities
                    ityManager.clear();
                }
            }
        });
    }

    @Override
    public Optional<Course> findById(Integer courseId) {
        log.debug("Searching for course with id={} in the database", courseId);
        return query(entityManager -> Optional.ofNullable(entityManager.find(Course.class, courseId)));
    }

    @Override
    public List<Course> findAll() {
        log.debug("Retrieving all courses from the database");
        return query(entityManager -> entityManager.createQuery("SELECT c FROM Course c", Course.class)
                .getResultList());
    }

    @Override
    public List<Course> findByCategory(Integer categoryId) {
        log.debug("Retrieving all courses belonging to category with id={} from the database", categoryId);
        return query(entityManager -> {
            TypedQuery<Course> query = entityManager.createQuery("SELECT c FROM Course c WHERE c.category.id = ?1",
                    Course.class);
            query.setParameter(1, categoryId);
            return query.getResultList();
        });
    }

    @Override
    public boolean existsById(Integer courseId) {
        log.debug("Checking whether course with id={} exists in the database", courseId);
        return query(entityManager -> {
            TypedQuery<Integer> query = entityManager.createQuery(
                    "SELECT c.id FROM Course c WHERE c.id = ?1",
                    Integer.class);
            query.setParameter(1, courseId);
            return !query.getResultList().isEmpty();
        });
    }

}
