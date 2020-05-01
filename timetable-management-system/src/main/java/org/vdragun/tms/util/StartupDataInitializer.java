package org.vdragun.tms.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.CategoryDao;
import org.vdragun.tms.dao.ClassroomDao;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.GroupDao;
import org.vdragun.tms.dao.StudentDao;
import org.vdragun.tms.dao.TeacherDao;
import org.vdragun.tms.dao.TimetableDao;
import org.vdragun.tms.util.generator.CategoryParser;
import org.vdragun.tms.util.generator.ClassroomGenerator;
import org.vdragun.tms.util.generator.CourseGenerator;
import org.vdragun.tms.util.generator.CourseGenerator.CourseGeneratorData;
import org.vdragun.tms.util.generator.GroupGenerator;
import org.vdragun.tms.util.generator.PersonGenerator.PersonGeneratorData;
import org.vdragun.tms.util.generator.StudentGenerator;
import org.vdragun.tms.util.generator.StudentsToCoursesRandomDistributor;
import org.vdragun.tms.util.generator.StudentsToGroupRandomDistributor;
import org.vdragun.tms.util.generator.TeacherGenerator;
import org.vdragun.tms.util.generator.TimetableGenerator;

/**
 * Initialize application with startup data
 * 
 * @author Vitaliy Dragun
 *
 */
public class StartupDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(StartupDataInitializer.class);

    @Value("#{'${generator.category}'.split(',\\s*')}")
    private List<String> categoryData;

    @Value("#{'${generator.firstNames}'.split(',\\s*')}")
    private List<String> firstNames;

    @Value("#{'${generator.lastNames}'.split(',\\s*')}")
    private List<String> lastNames;

    @Value("#{T(java.time.LocalDate).parse('${genertor.baseDate}')}")
    private LocalDate baseDate;

    @Value("${generator.deviationDays}")
    private Integer deviationDays;

    @Value("${generator.numberOfStudents}")
    private Integer numberOfStudents;

    @Value("${generator.numberOfTeachers}")
    private Integer numberOfTeachers;

    @Value("${generator.numberOfClassrooms}")
    private Integer numberOfClassroms;

    @Value("${generator.classroomFromCapacity}")
    private Integer classroomFromCapacity;

    @Value("${generator.classroomToCapacity}")
    private Integer classroomToCapacity;

    @Value("#{'${generator.coursePrefixes}'.split(',\\s*')}")
    private List<String> coursePrefixes;

    @Value("${generator.numberOfCourses}")
    private Integer numberOfCourses;

    @Value("${generator.numberOfGroups}")
    private Integer numberOfGroups;

    @Value("${generator.minStudentsPerGroup}")
    private Integer minStudentsPerGroup;
    
    @Value("${generator.maxStudentsPerGroup}")
    private Integer maxStudenstPerGroup;

    @Value("${generator.maxCoursesPerStudent}")
    private Integer maxCoursesPerStudent;

    @Value("#{T(java.time.LocalTime).parse('${generator.timetable.startTime}')}")
    private LocalTime timetableStartTime;

    @Value("#{T(java.time.LocalTime).parse('${generator.timetable.endTime}')}")
    private LocalTime timetableEndTime;

    @Value("${generator.timetable.numberOfMonths}")
    private Integer numberOfMonths;

    @Value("${generator.timetable.durationInMinutes}")
    private Integer durationInMinutes;

    @Value("${generator.timetable.maxClassesPerWeek}")
    private Integer maxClassesPerWeek;

    private DataSource dataSource;

    private ClassroomDao classroomDao;

    private CategoryDao categoryDao;

    private GroupDao groupDao;

    private TeacherDao teacherDao;

    private StudentDao studentDao;

    private CourseDao courseDao;

    private TimetableDao timetableDao;

    public StartupDataInitializer(
            DataSource dataSource,
            ClassroomDao classroomDao,
            CategoryDao categoryDao,
            GroupDao groupDao,
            TeacherDao teacherDao,
            StudentDao studentDao,
            CourseDao courseDao,
            TimetableDao timetableDao) {
        this.dataSource = dataSource;
        this.classroomDao = classroomDao;
        this.categoryDao = categoryDao;
        this.groupDao = groupDao;
        this.teacherDao = teacherDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.timetableDao = timetableDao;
    }

    @PostConstruct
    public void initialize() {
        createDatabaseSchema();
        populateDatabaseWithInitData();
    }

    private void createDatabaseSchema() {
        LOG.info("Creating database schema");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("/sql/db_schema.sql"));
        databasePopulator.execute(dataSource);
    }

    private void populateDatabaseWithInitData() {
        LOG.info("Populating database with init data");
        CategoryParser categoryParser = new CategoryParser();
        GroupGenerator groupGenerator = new GroupGenerator();
        ClassroomGenerator classroomGenerator = new ClassroomGenerator();
        StudentGenerator studentGenerator = new StudentGenerator();
        TeacherGenerator teacherGenerator = new TeacherGenerator();
        CourseGenerator courseGenerator = new CourseGenerator();
        TimetableGenerator timetableGenerator = new TimetableGenerator(
                timetableStartTime,
                timetableEndTime,
                durationInMinutes,
                numberOfMonths,
                maxClassesPerWeek);
        StudentsToGroupRandomDistributor groupRandomDistributor = new StudentsToGroupRandomDistributor();
        StudentsToCoursesRandomDistributor coursesRandomDistributor = new StudentsToCoursesRandomDistributor();

        List<Category> categories = categoryParser.parse(categoryData);
        categoryDao.saveAll(categories);

        List<Group> groups = groupGenerator.generate(numberOfGroups);
        groupDao.saveAll(groups);
        
        List<Classroom> classrooms = classroomGenerator.generate(numberOfClassroms, classroomFromCapacity,
                classroomToCapacity);
        classrooms.forEach(classroom -> classroomDao.save(classroom));

        List<Student> students = studentGenerator
                .generate(PersonGeneratorData.from(numberOfStudents, firstNames, lastNames, baseDate, deviationDays));
        studentDao.saveAll(students);

        List<Teacher> teachers = teacherGenerator
                .generate(PersonGeneratorData.from(numberOfTeachers, firstNames, lastNames, baseDate, deviationDays));
        teacherDao.saveAll(teachers);

        List<Course> courses = courseGenerator
                .generate(CourseGeneratorData.from(numberOfCourses, coursePrefixes, categories, teachers));
        courseDao.saveAll(courses);

        List<Timetable> timetables = timetableGenerator.generate(courses, classrooms);
        timetableDao.saveAll(timetables);

        groupRandomDistributor.assignStudentsToGroups(students, groups, minStudentsPerGroup, maxStudenstPerGroup);
        persistStudentsWithGroupsInDatabase(students);

        coursesRandomDistributor.assignStudentsToCourses(students, courses, maxCoursesPerStudent);
        persistStudentWithCoursesInDatabase(students);
    }

    private void persistStudentWithCoursesInDatabase(List<Student> students) {
        students.stream()
                .filter(student -> !student.getCourses().isEmpty())
                .forEach(student -> student.getCourses()
                        .forEach(course -> studentDao.addToCourse(student.getId(), course.getId())));
    }

    private void persistStudentsWithGroupsInDatabase(List<Student> students) {
        students.stream()
                .filter(student -> student.getGroup() != null)
                .forEach(student -> studentDao.addToGroup(student.getId(), student.getGroup().getId()));
    }
}
