package org.vdragun.tms.core.application.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.CourseData;
import org.vdragun.tms.core.application.service.CourseService;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.CategoryDao;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.TeacherDao;

/**
 * Default implementation of {@link CourseService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class CourseServiceImpl implements CourseService {

    private CourseDao courseDao;
    private CategoryDao categoryDao;
    private TeacherDao teacherDao;

    public CourseServiceImpl(CourseDao courseDao, CategoryDao categoryDao, TeacherDao teacherDao) {
        this.courseDao = courseDao;
        this.categoryDao = categoryDao;
        this.teacherDao = teacherDao;
    }

    @Override
    public Course registerNewCourse(CourseData courseData) {
        Category category = requireExistingCategory(courseData.getCategoryId());
        Teacher teacher = requireExistingTeacher(courseData.getTeacherId());
        Course course = new Course(courseData.getName(), courseData.getDescription(), category, teacher);
        courseDao.save(course);
        return course;
    }

    @Override
    public Course findCourseById(Integer courseId) {
        return courseDao.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id=%d not found", courseId));
    }

    @Override
    public List<Course> findAllCourses() {
        return courseDao.findAll();
    }

    @Override
    public List<Course> findCoursesByCategory(Integer categoryId) {
        return courseDao.findByCategory(categoryId);
    }

    private Teacher requireExistingTeacher(Integer teacherId) {
        Optional<Teacher> result = teacherDao.findById(teacherId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("Fail to register new course: teacher with id=%d does not exist",
                    teacherId);
        }
        return result.get();
    }

    private Category requireExistingCategory(Integer categoryId) {
        Optional<Category> result = categoryDao.findById(categoryId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("Fail to register new course: category with id=%d does not exist",
                    categoryId);
        }
        return result.get();
    }

}
