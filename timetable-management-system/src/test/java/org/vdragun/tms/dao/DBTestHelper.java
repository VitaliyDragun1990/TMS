package org.vdragun.tms.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.vdragun.tms.core.domain.Title;

/**
 * Contains helper methods to facilitate DAO testing.
 * 
 * @author Vitaliy Dragun
 *
 */
public class DBTestHelper {

    @PersistenceContext
    private EntityManager em;

    public Classroom saveClassroomToDatabase(int capacity) {
        Classroom classroom = new Classroom(capacity);
        em.persist(classroom);
        return classroom;
    }

    public List<Classroom> findAllClassroomsInDatabase() {
        return em.createQuery("SELECT c FROM Classroom c", Classroom.class).getResultList();
    }

    public Category saveCategoryToDatabase(String code, String description) {
        Category category = new Category(code, description);
        em.persist(category);
        return category;
    }

    public List<Category> findAllCategoriesInDatabase() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    public Group saveGroupToDatabase(String name) {
        Group group = new Group(name);
        em.persist(group);
        return group;
    }

    public List<Group> findAllGroupsInDatabase() {
        return em.createQuery("SELECT g FROM Group g", Group.class).getResultList();
    }

    public Teacher saveTeacherToDatabase(String firstName, String lastName, Title title, LocalDate dateHired) {
        Teacher teacher = new Teacher(firstName, lastName, title, dateHired);
        em.persist(teacher);
        return teacher;
    }

    public List<Teacher> findAllTeachersInDatabase() {
        return em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    }

    public Course saveCourseToDatabase(String name, String desc, Category category, Teacher teacher) {
        Course course = new Course(name, desc, category, teacher);
        teacher.addCourse(course);
        em.persist(teacher);
        return course;
    }

    public Student saveStudentToDatabase(String firstName, String lastName, LocalDate enrollmentDate, Group group) {
        Student student = new Student(firstName, lastName, enrollmentDate);
        student.setGroup(group);
        em.persist(student);
        return student;
    }

    public Student saveStudentToDatabase(String firstName, String lastName, LocalDate enrollmentDate) {
        return saveStudentToDatabase(firstName, lastName, enrollmentDate, null);
    }

    public List<Student> findAllStudentsInDatabase() {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    public void addStudentsToCourseInDatabase(Course course, Student... students) {
        for (Student s : students) {
            s.addCourse(course);
            em.persist(s);
        }
    }

    public void addStudentToCoursesInDatabase(Student student, Course... courses) {
        for (Course c : courses) {
            student.addCourse(c);
        }
        em.persist(student);
    }

    public void addStudentsToGroupInDatabase(Group group, Student... students) {
        for (Student s : students) {
            s.setGroup(group);
            em.persist(s);
        }
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

    public Timetable saveTimetableToDatabase(LocalDateTime startTime, int duration, Course course,
            Teacher teacher, Classroom classroom) {
        Timetable timetable = new Timetable(startTime, duration, course, classroom, teacher);
        em.persist(timetable);
        return timetable;
    }

    public List<Timetable> findAllTimetablesInDatabase() {
        return em.createQuery("SELECT t FROM Timetable t", Timetable.class).getResultList();
    }
}
