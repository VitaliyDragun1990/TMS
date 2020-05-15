package org.vdragun.tms.core.application.service.course;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
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
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

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
        LOG.debug("Registering new course using data: {}", courseData);

        Category category = getCategory(courseData.getCategoryId());
        Teacher teacher = getTeacher(courseData.getTeacherId());
        Course course = new Course(courseData.getName(), courseData.getDescription(), category, teacher);
        courseDao.save(course);

        LOG.debug("New course has been registered: {}", course);

        return course;
    }

    @Override
    @Transactional(readOnly = true)
    public Course findCourseById(Integer courseId) {
        LOG.debug("Searching for course with id={}", courseId);

        return courseDao.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id=%d not found", courseId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findAllCourses() {
        LOG.debug("Retrieving all courses");

        List<Course> result = courseDao.findAll();

        LOG.debug("Found {} courses", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findCoursesByCategory(Integer categoryId) {
        LOG.debug("Retrieving courses belonging to category with id={}", categoryId);

        List<Course> result = courseDao.findByCategoryId(categoryId);

        LOG.debug("Found {} courses belonging to category with id={}", result.size(), categoryId);
        return result;
    }

    private Teacher getTeacher(Integer teacherId) {
        Optional<Teacher> result = teacherDao.findById(teacherId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("Fail to register new course: teacher with id=%d does not exist",
                    teacherId);
        }
        return result.get();
    }

    private Category getCategory(Integer categoryId) {
        Optional<Category> result = categoryDao.findById(categoryId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("Fail to register new course: category with id=%d does not exist",
                    categoryId);
        }
        return result.get();
    }

}
