package org.vdragun.tms.system;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Configuration.headless;
import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.system.page.HomePage;
import org.vdragun.tms.system.page.StudentInfoPage;
import org.vdragun.tms.system.page.StudentRegistrationPage;
import org.vdragun.tms.system.page.StudentUpdatePage;
import org.vdragun.tms.util.localizer.MessageLocalizer;

import com.codeborne.selenide.Browsers;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = {
                "jndi.datasource=false",
                "startup.data.initialize=false" })
@Import({
        EmbeddedDataSourceConfig.class
})
@DisplayName("Web User Interface System Test")
public class WebUserInterfaceSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MessageLocalizer msgLocalizer;

    @BeforeEach
    void setSelenide() {
        browser = Browsers.CHROME;
        headless = true;
        timeout = TimeUnit.SECONDS.toMillis(10);
        baseUrl = String.format("http://localhost:%d", port);
        fastSetValue = true;
        reportsFolder = "target/reports/tests";
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldCorrectlyDisplayHomePage() {
        HomePage
                .open()
                .verifyTitle(complexMessage("%s - %s", "title.main", "title.home"))
                .verifyHeader(message("msg.welcome"));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldRegisterNewStudentAndDisplaySuccessMessage() {
        StudentRegistrationPage
                .open()
                .provideInput("Martin", "Fowler", "10/05/2020")
                .submitExpectedSuccess()
                .verifyInfoMsgPresent(message("msg.studentRegisterSuccess"))
                .verifyPageUrl("/students/1")
                .verifyFormValues("Martin", "Fowler", "-");
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldShowValidationErrorsIfRegistrationDataInvalid() {
        StudentRegistrationPage
                .open()
                .provideInput("a", "b", "")
                .submitExpectedFailure()
                .verifyPageUrl("/students")
                .verifyFormErrorMsgPresent(message("msg.correctErrors"))
                .verifyFirstNameErorrs(message("PersonName"))
                .verifyLastNameErorrs(message("PersonName"))
                .verifyEnrollmentDateErorrs(message("NotNull.enrollmentDate"));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldUpdateExistingStudentAndDisplaySuccessMessage() {
        StudentUpdatePage
                .openForStudent(1000)
                .provideInput("Jacky", "Brown", "-", 1000, 1001)
                .submit()
                .verifyInfoMsgPresent(message("msg.studentUpdateSuccess"))
                .verifyPageUrl("/students/1000")
                .verifyFormValues("Jacky", "Brown", "-", 1000, 1001);
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldSearchStudentMonthlyTimetablesAndDisplayResult() {
        int expectedTimetablesCount = 2;

        StudentInfoPage
                .openForStudent(1000)
                .searchMonthlyTimetablesFor("May")
                .verifyInfoMsg(message("msg.timetablesForStudent", expectedTimetablesCount, "John", "Doe", "May"))
                .verifyTimetablesCount(expectedTimetablesCount);
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldDisplayErrorMessageIfSearchStudentTimetablesWithoutTargetDate() {
        StudentInfoPage
                .openForStudent(1000)
                .searchTimetablesExpectedFailure("monthly", "")
                .verifySearchTimetablesErrorMessage(message("error.targetDateRequired"));
    }

    /**
     * Returns localized message
     * 
     * @param template - formatting template for complex messages
     * @param msgCodes - message codes to look localized messages with
     */
    private String complexMessage(String template, String... msgCodes) {
        Object[] args = Arrays.stream(msgCodes)
                .map(code -> msgLocalizer.getLocalizedMessage(code))
                .collect(toList())
                .toArray(new Object[0]);

        return format(template, args);
    }

    /**
     * Returns localized message using provided message code argument
     */
    private String message(String msgCode, Object... args) {
        return msgLocalizer.getLocalizedMessage(msgCode, args);
    }

}