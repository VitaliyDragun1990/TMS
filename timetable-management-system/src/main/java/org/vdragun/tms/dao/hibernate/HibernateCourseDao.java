package org.vdragun.tms.dao.hibernate;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.dao.CourseDao;

/**
 * Implementation of {@link CourseDao} using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class HibernateCourseDao extends BaseHibernateDao implements CourseDao {

    public HibernateCourseDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void save(Course course) {
        log.debug("Saving new course to the database: {}", course);
        execute(session -> session.save(course));
    }

    @Override
    public void saveAll(List<Course> courses) {
        log.debug("Saving {} new courses to the database", courses.size());
        int batchSize = getBatchSize();
        execute(session -> {
            for (int i = 0; i < courses.size(); i++) {
                // saves course instance into active session (but not in the database)
                session.persist(courses.get(i));
                if (i % batchSize == 0 || i == courses.size() - 1) {
                    // flush changes to the database (actually save courses into the database)
                    session.flush();
                    // clear session from all persisted entities
                    session.clear();
                }
            }
        });
    }

    @Override
    public Optional<Course> findById(Integer courseId) {
        log.debug("Searching for course with id={} in the database", courseId);
        return query(session -> Optional.ofNullable(session.get(Course.class, courseId)));
    }

    @Override
    public List<Course> findAll() {
        log.debug("Retrieving all courses from the database");
        return query(session -> session.createQuery("SELECT c FROM Course c", Course.class).list());
    }

    @Override
    public List<Course> findByCategory(Integer categoryId) {
        log.debug("Retrieving all courses belonging to category with id={} from the database", categoryId);
        return query(session -> {
            Query<Course> query = session.createQuery("SELECT c FROM Course c WHERE c.category.id = ?1", Course.class);
            query.setParameter(1, categoryId);
            return query.list();
        });
    }

    @Override
    public boolean existsById(Integer courseId) {
        log.debug("Checking whether course with id={} exists in the database", courseId);
        return query(session -> {
            Query<Integer> query = session.createQuery(
                    "SELECT c.id FROM Course c WHERE c.id = ?1",
                    Integer.class);
            query.setParameter(1, courseId);
            return !query.list().isEmpty();
        });
    }

}
