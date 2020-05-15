package org.vdragun.tms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;

/**
 * Contains helper methods to facilitate DAO testing.
 * 
 * @author Vitaliy Dragun
 *
 */
public class DBTestHelper {

    @PersistenceContext
    private EntityManager em;

    public List<Classroom> findAllClassroomsInDatabase() {
        return em.createQuery("SELECT c FROM Classroom c", Classroom.class).getResultList();
    }

    public Category findCategoryByCodeInDatabase(String categoryCode) {
        TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c WHERE c.code = ?1", Category.class);
        query.setParameter(1, categoryCode);
        return query.getSingleResult();
    }

    public Classroom findRandomClassroomInDatabase() {
        return em.createQuery("SELECT c FROM Classroom c", Classroom.class).getResultList().get(0);
    }

    public Group findGroupByNameInDatabase(String groupName) {
        TypedQuery<Group> query = em.createQuery("SELECT g FROM Group g WHERE g.name = ?1", Group.class);
        query.setParameter(1, groupName);
        return query.getSingleResult();
    }

    public Course findCourseByNameInDatabase(String courseName) {
        TypedQuery<Course> query = em.createQuery("SELECT c FROM Course c WHERE c.name = ?1", Course.class);
        query.setParameter(1, courseName);
        return query.getSingleResult();
    }

    public Course findRandomCourseInDatabase() {
        return em.createQuery("SELECT c FROM Course c", Course.class).getResultList().get(0);
    }

    public Teacher findRandomTeacherInDatabase() {
        return em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList().get(0);
    }

    public Teacher findTeacherByNameInDatabase(String firstName, String lastName) {
        TypedQuery<Teacher> query = em.createQuery("SELECT t FROM Teacher t WHERE t.firstName = ?1 AND t.lastName = ?2",
                Teacher.class);
        query.setParameter(1, firstName);
        query.setParameter(2, lastName);
        return query.getSingleResult();
    }

    public Student findStudentByNameInDatabase(String firstName, String lastName) {
        TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s WHERE s.firstName = ?1 AND s.lastName = ?2",
                Student.class);
        query.setParameter(1, firstName);
        query.setParameter(2, lastName);
        return query.getSingleResult();
    }

    public Student findStudentByIdInDatabase(Integer studentId) {
        return em.find(Student.class, studentId);
    }

    public Timetable findRandomTimetableInDatabase() {
        return em.createQuery("SELECT t FROM Timetable t", Timetable.class).getResultList().get(0);
    }

    public Classroom findClassroomByCapacity(int classroomCapacity) {
        TypedQuery<Classroom> query = em.createQuery("SELECT c FROM Classroom c WHERE c.capacity = ?1",
                Classroom.class);
        query.setParameter(1, classroomCapacity);
        return query.getSingleResult();
    }

    public List<Category> findAllCategoriesInDatabase() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    public List<Group> findAllGroupsInDatabase() {
        return em.createQuery("SELECT g FROM Group g", Group.class).getResultList();
    }

    public List<Teacher> findAllTeachersInDatabase() {
        return em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    }

    public List<Student> findAllStudentsInDatabase() {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    public List<Course> findAllStudentCoursesInDatabase(Student student) {
        return new ArrayList<>(em.find(Student.class, student.getId()).getCourses());
    }

    public List<Student> findAllGroupStudentsInDatabase(Group group) {
        TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s JOIN s.group g WHERE g.id = ?1",
                Student.class);
        query.setParameter(1, group.getId());
        return query.getResultList();
    }

    public List<Course> findAllCoursesInDatabase() {
        return em.createQuery("SELECT c FROM Course c", Course.class).getResultList();
    }

    public List<Timetable> findAllTimetablesInDatabase() {
        return em.createQuery("SELECT t FROM Timetable t", Timetable.class).getResultList();
    }
}
