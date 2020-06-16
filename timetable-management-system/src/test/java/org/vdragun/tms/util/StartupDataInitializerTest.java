package org.vdragun.tms.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.util.StartupDataInitializerTest.StartupDataConfig;
import org.vdragun.tms.util.initializer.GeneratorProperties;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ DaoTestConfig.class, StartupDataConfig.class })
@DisplayName("Startup Data Initializer")
class StartupDataInitializerTest {

    @Autowired
    private DBTestHelper dbHelper;

    @Autowired
    private GeneratorProperties generatorProps;

    @Test
    void shouldPopulateDatabaseWithInitDataOnStartup() throws SQLException {
        int studentsInDatabase = dbHelper.findAllStudentsInDatabase().size();
        int teachersInDatabase = dbHelper.findAllTeachersInDatabase().size();
        int categoriesInDatabase = dbHelper.findAllCategoriesInDatabase().size();
        int classroomsInDatabase = dbHelper.findAllClassroomsInDatabase().size();
        int groupsInDatabase = dbHelper.findAllGroupsInDatabase().size();
        int coursesInDatabase = dbHelper.findAllCoursesInDatabase().size();
        int timetablesInDatabase = dbHelper.findAllTimetablesInDatabase().size();

        assertThat(studentsInDatabase, equalTo(generatorProps.getNumberOfStudents()));
        assertThat(teachersInDatabase, equalTo(generatorProps.getNumberOfTeachers()));
        assertThat(categoriesInDatabase, equalTo(generatorProps.getCategories().size()));
        assertThat(classroomsInDatabase, equalTo(generatorProps.getNumberOfClassrooms()));
        assertThat(groupsInDatabase, equalTo(generatorProps.getNumberOfGroups()));
        assertThat(coursesInDatabase, equalTo(generatorProps.getNumberOfCourses()));
        assertThat(timetablesInDatabase, greaterThanOrEqualTo(
                generatorProps.getTimetablePeriodOfMonths() * 4 * coursesInDatabase));
    }

    @TestConfiguration
    @ComponentScan(basePackages = { "org.vdragun.tms.util.initializer" })
    static class StartupDataConfig {

    }

}
