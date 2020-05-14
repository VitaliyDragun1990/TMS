package org.vdragun.tms.dao.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;

/**
 * Implementation of {@link TeacherDao} using Spring Data
 * 
 * @author Vitaliy Dragun
 *
 */
@RepositoryDefinition(domainClass = Teacher.class, idClass = Integer.class)
public interface SpringDataTeacherDao extends TeacherDao {

    @Override
    @EntityGraph(attributePaths = "courses")
    Optional<Teacher> findById(Integer teacherId);

    @Override
    @EntityGraph(attributePaths = "courses")
    List<Teacher> findAll();

    @Override
    @Query("SELECT t FROM Course c JOIN c.teacher t JOIN FETCH t.courses WHERE c.id = ?1")
    Optional<Teacher> findForCourseWithId(Integer courseId);
}
