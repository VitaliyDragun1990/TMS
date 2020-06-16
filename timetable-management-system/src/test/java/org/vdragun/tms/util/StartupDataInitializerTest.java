package org.vdragun.tms.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.util.StartupDataInitializerTest.StartupDataConfig;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ DaoTestConfig.class, StartupDataConfig.class })
@DisplayName("Startup Data Initializer")
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
    private DBTestHelper dbHelper;

    @Test
    void shouldPopulateDatabaseWithInitDataOnStartup() throws SQLException {
        int studentsInDatabase = dbHelper.findAllStudentsInDatabase().size();
        int teachersInDatabase = dbHelper.findAllTeachersInDatabase().size();
        int categoriesInDatabase = dbHelper.findAllCategoriesInDatabase().size();
        int classroomsInDatabase = dbHelper.findAllClassroomsInDatabase().size();
        int groupsInDatabase = dbHelper.findAllGroupsInDatabase().size();
        int coursesInDatabase = dbHelper.findAllCoursesInDatabase().size();
        int timetablesInDatabase = dbHelper.findAllTimetablesInDatabase().size();

        assertThat(studentsInDatabase, equalTo(expectedNumberOfStudents));
        assertThat(teachersInDatabase, equalTo(expectedNumberOfTeachers));
        assertThat(categoriesInDatabase, equalTo(expectedNumberOfCategories));
        assertThat(classroomsInDatabase, equalTo(expectedNumberOfClassroms));
        assertThat(groupsInDatabase, equalTo(expectedNumberOfGroups));
        assertThat(coursesInDatabase, equalTo(expectedNumberOfCourses));
        assertThat(timetablesInDatabase, greaterThanOrEqualTo(numberOfMonths * 4 * coursesInDatabase));
    }

    @TestConfiguration
    @ComponentScan(basePackages = { "org.vdragun.tms.util.initializer" })
    static class StartupDataConfig {

    }

}
