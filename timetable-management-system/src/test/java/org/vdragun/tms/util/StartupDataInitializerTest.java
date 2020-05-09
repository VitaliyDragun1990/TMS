package org.vdragun.tms.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.vdragun.tms.config.JdbcDaoConfig;
import org.vdragun.tms.config.StartupDataConfig;
import org.vdragun.tms.dao.jdbc.DBTestConfig;
import org.vdragun.tms.dao.jdbc.JdbcTestHelper;

@DisplayName("Startup Data Initializer")
@SpringJUnitConfig({ DBTestConfig.class, JdbcDaoConfig.class, StartupDataConfig.class })
class StartupDataInitializerTest {

    @Value("#{'${generator.category}'.split(',\\s*').length}")
    private Integer expectedNumberOfCategories;

    @Value("${generator.numberOfStudents}")
    private Integer expectedNumberOfStudents;

    @Value("${generator.numberOfTeachers}")
    private Integer expectedNumberOfTeachers;

    @Value("${generator.numberOfClassrooms}")
    private Integer expectedNumberOfClassroms;

    @Value("${generator.numberOfCourses}")
    private Integer expectedNumberOfCourses;

    @Value("${generator.numberOfGroups}")
    private Integer expectedNumberOfGroups;

    @Value("${generator.timetable.numberOfMonths}")
    private Integer numberOfMonths;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldPopulateDatabaseWithInitDataOnStartup() throws SQLException {
        int studentsInDatabase = jdbcHelper.findAllStudentsInDatabase().size();
        int teachersInDatabase = jdbcHelper.findAllTeachersInDatabase().size();
        int categoriesInDatabase = jdbcHelper.findAllCategoriesInDatabase().size();
        int classroomsInDatabase = jdbcHelper.findAllClassroomsInDatabase().size();
        int groupsInDatabase = jdbcHelper.findAllGroupsInDatabase().size();
        int coursesInDatabase = jdbcHelper.findAllCoursesInDatabase().size();
        int timetablesInDatabase = jdbcHelper.findAllTimetablesInDatabase().size();

        assertThat(studentsInDatabase, equalTo(expectedNumberOfStudents));
        assertThat(teachersInDatabase, equalTo(expectedNumberOfTeachers));
        assertThat(categoriesInDatabase, equalTo(expectedNumberOfCategories));
        assertThat(classroomsInDatabase, equalTo(expectedNumberOfClassroms));
        assertThat(groupsInDatabase, equalTo(expectedNumberOfGroups));
        assertThat(coursesInDatabase, equalTo(expectedNumberOfCourses));
        assertThat(timetablesInDatabase, greaterThanOrEqualTo(numberOfMonths * 4 * coursesInDatabase));
    }

}
