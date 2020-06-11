package org.vdragun.tms.system;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Configuration.headless;
import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static com.codeborne.selenide.WebDriverRunner.url;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.vdragun.tms.EmbeddedDataSourceConfig;
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
        // Open the home page
        open("/");

        assertThat(title()).isEqualTo(complexMessage("%s - %s", "title.main", "title.home"));
        $("#mainContent h1").shouldHave(text(message("msg.welcome")));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldRegisterNewStudentAndDisplaySuccessMessage() {
        // Open the home page
        open("/");
        // navigate to registration form
        $(byCssSelector("[href='#studentSubmenu']")).click();
        $("a#registerStudent").click();
        // verify correct url
        assertThat(url()).endsWith("/students/register");

        // input valid values
        $("input#firstName").setValue("Martin");
        $("input#lastName").setValue("Fowler");
        $("input#enrollmentDate").setValue("10/05/2020");
        // press submit button
        $(byCssSelector("[type='submit']")).click();

        // verify correct url
        assertThat(url()).endsWith("/students/1");
        // verify display message
        assertThat($("#infoMsgText").text()).isEqualTo(message("msg.studentRegisterSuccess"));
        // verify updated data
        $("input#firstName").shouldHave(value("Martin"));
        $("input#lastName").shouldHave(value("Fowler"));
        $("input#enrollmentDate").shouldHave(value("10/05/2020"));
        $$("select#group option").shouldHave(size(1));
        $$("select#group option").get(0).shouldHave(text("-"));
        $$("div#studentCourses input[type='checkbox']").shouldHave(size(0));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldShowValidationErrorsIfRegistrationDataInvalid() {
        // Open the home page
        open("/");
        // navigate to registration form
        $(byCssSelector("[href='#studentSubmenu']")).click();
        $("a#registerStudent").click();
        // verify correct url
        assertThat(url()).endsWith("/students/register");

        // input invalid values
        $("input#firstName").setValue("a");
        $("input#lastName").setValue("b");
        $("input#enrollmentDate").setValue("");
        // press submit button
        $(byCssSelector("[type='submit']")).click();

        // verify correct url
        assertThat(url()).endsWith("/students");
        // verify validation messages
        assertThat($("p.form-errors").text()).isEqualTo(message("msg.correctErrors"));
        $$("span.first-name-errors ul li").shouldHave(size(1));
        $$("span.first-name-errors ul li").shouldHave(texts(message("PersonName")));
        $$("span.last-name-errors ul li").shouldHave(size(1));
        $$("span.last-name-errors ul li").shouldHave(texts(message("PersonName")));
        $$("span.date-errors ul li").shouldHave(size(1));
        $$("span.date-errors ul li").shouldHave(texts(message("NotNull.enrollmentDate")));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldUpdateExistingStudentAndDisplaySuccessMessage() {
        // Open the home page
        open("/");
        // navigate to registration form
        $(byCssSelector("[href='#studentSubmenu']")).click();
        $("a#showAllStudents").click();
        // verify correct url
        assertThat(url()).endsWith("/students");
        // select first student
        $$("table#studentsTable tbody tr").first().$("a.btn-link").click();
        // verify correct url
        assertThat(url()).endsWith("/students/1000");
        // click update button
        $("a.updateBtn").click();
        // verify correct url
        assertThat(url()).endsWith("/students/1000/update");

        // input valid values
        $("input#firstName").setValue("Jacky");
        $("input#lastName").setValue("Brown");
        $("select#groupId").selectOption("-");
        $$(byCssSelector("[name='courseIds']")).get(0).setSelected(true);
        $$(byCssSelector("[name='courseIds']")).get(1).setSelected(true);
        // press update button
        $("#updateDialogBtn").click();
        $("#confirmUpdateBtn").click();

        // verify correct url
        assertThat(url()).endsWith("/students/1000");
        // verify display message
        assertThat($("#infoMsgText").text()).isEqualTo(message("msg.studentUpdateSuccess"));
        // verify updated data
        $("input#firstName").shouldHave(value("Jacky"));
        $("input#lastName").shouldHave(value("Brown"));
        $$("select#group option").shouldHave(size(1));
        $$("select#group option").get(0).shouldHave(text("-"));
        $$("div#studentCourses input[type='checkbox']").shouldHave(size(2));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldSearchStudentMonthlyTimetablesAndDisplayResult() {
        // Open the home page
        open("/");
        // navigate to registration form
        $(byCssSelector("[href='#studentSubmenu']")).click();
        $("a#showAllStudents").click();
        // verify correct url
        assertThat(url()).endsWith("/students");
        // select first student
        $$("table#studentsTable tbody tr").first().$("a.btn-link").click();
        // verify correct url
        assertThat(url()).endsWith("/students/1000");

        // select monthly search
        $(By.name("inlineRadioOptions")).selectRadio("monthly");
        // input search month
        $("input#dateTimePicker").setValue("May");
        // press search button
        $("button#searchTmBtn").click();

        // verify correct url
        assertThat(url()).endsWith("/timetables/student/1000/month?targetDate=May");
        // verify correct search result
        int expectedTimetablesCount = 2;
        $("h4#headerMsg").shouldHave(
                text(
                        message("msg.timetablesForStudent", expectedTimetablesCount, "John", "Doe", "May")));
        $$("table#timetablesTable tbody tr").shouldHave(size(expectedTimetablesCount));
    }

    @Test
    @Rollback
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/ui_test_data.sql" })
    void shouldDisplayErrorMessageIfSearchStudentTimetablesWithoutTargetDate() {
        // Open the home page
        open("/");
        // navigate to registration form
        $(byCssSelector("[href='#studentSubmenu']")).click();
        $("a#showAllStudents").click();
        // verify correct url
        assertThat(url()).endsWith("/students");
        // select first student
        $$("table#studentsTable tbody tr").first().$("a.btn-link").click();
        // verify correct url
        assertThat(url()).endsWith("/students/1000");

        // select monthly search
        $(By.name("inlineRadioOptions")).selectRadio("monthly");
        // press search button
        $("button#searchTmBtn").click();

        // verify url hasn't changed
        assertThat(url()).endsWith("/students/1000");
        $("div#invalidDateInput").shouldBe(visible);
        $("div#invalidDateInput").shouldHave(text(message("error.targetDateRequired")));
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
