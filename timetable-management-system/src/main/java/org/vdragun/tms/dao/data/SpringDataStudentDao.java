package org.vdragun.tms.dao.data;

import java.util.List;
import java.util.Optional;

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
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = ?1")
    Optional<Student> findById(Integer studentId);

    @Override
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses")
    List<Student> findAll();

    @Override
    @Query("SELECT DISTINCT s FROM Student s JOIN FETCH s.courses "
            + "WHERE s.id IN (SELECT st.id FROM Student st JOIN st.courses c WHERE c.id = ?1)")
    List<Student> findForCourse(Integer courseId);

    @Override
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses c WHERE s.group.id = ?1")
    List<Student> findForGroup(Integer groupId);
}
