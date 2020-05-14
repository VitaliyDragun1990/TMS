package org.vdragun.tms.dao.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.StudentDao;

/**
 * Implementation of {@link StudentDao} using Spring Data
 * 
 * @author Vitaliy Dragun
 *
 */
@RepositoryDefinition(domainClass = Student.class, idClass = Integer.class)
public interface SpringDataStudentDao extends StudentDao, StudentDaoFragment {

    @Override
    @EntityGraph(attributePaths = { "courses", "group" })
    Optional<Student> findById(Integer studentId);

    @Override
    @EntityGraph(attributePaths = { "courses", "group" })
    List<Student> findAll();

    @Override
    @Query("SELECT DISTINCT s FROM Student s JOIN FETCH s.courses "
            + "WHERE s.id IN (SELECT st.id FROM Student st JOIN st.courses c WHERE c.id = ?1)")
    List<Student> findByCourseId(Integer courseId);

    @Override
    @EntityGraph(attributePaths = { "courses", "group" })
    List<Student> findByGroupId(Integer groupId);
}
