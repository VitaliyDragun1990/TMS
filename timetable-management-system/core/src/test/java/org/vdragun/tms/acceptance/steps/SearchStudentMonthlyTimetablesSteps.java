package org.vdragun.tms.acceptance.steps;

import static java.time.Month.MAY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import javax.sql.DataSource;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;

public class SearchStudentMonthlyTimetablesSteps {

    private static final int STUDENT_ID = 1000;

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private DataSource dataSource;

    @Value("${db.clear_script}")
    private String clearScript;

    @Value("${db.course_three_timetables_script}")
    private String courseTimetablesScript;

    @Value("${db.student_course_reg_script}")
    private String studentCourseRegScript;

    private List<Timetable> searchResult;

    @Given("$number course timetables for month")
    public void courseTimetablesForMonth(@Named("number") int numberOfTimetables) {
        executeScripts(clearScript, courseTimetablesScript);
    }

    @Given("student is registered for given course")
    public void studentRegisteredForGivenCourse() {
        executeScripts(studentCourseRegScript);
    }

    @When("student monthly timetables search is conducted")
    public void timetablesSearchIsConducted() {
        searchResult = timetableService.findMonthlyTimetablesForStudent(STUDENT_ID, MAY);
    }

    @Then("empty result is returned")
    public void searchResultIsEmpty() {
        assertThat(searchResult, hasSize(0));
    }

    @Then("result with $number timetables returned")
    public void searchResultWithTimetables(@Named("number") int expectedNumber) {
        assertThat(searchResult, hasSize(expectedNumber));
    }

    private void executeScripts(String... scripts) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        for (String script : scripts) {
            databasePopulator.addScript(new ClassPathResource(script));
        }
        databasePopulator.execute(dataSource);
    }
}
