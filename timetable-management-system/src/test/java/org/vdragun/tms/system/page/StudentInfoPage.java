package org.vdragun.tms.system.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;

import com.codeborne.selenide.Selenide;

/**
 * Page object for student info page
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentInfoPage {

    private Integer studentId;

    public static StudentInfoPage openForStudent(Integer studentId) {
        // Open the home page
        Selenide.open("/");
        // navigate to registration form
        $(byCssSelector("[href='#studentSubmenu']")).click();
        $("a#showAllStudents").click();
        // verify correct page URL
        assertThat(url()).endsWith("/students");
        // select student with given id
        $$(format("table#studentsTable tbody tr#row-%d", studentId)).first().$("a.btn-link").click();
        // verify correct page URL
        assertThat(url()).endsWith(format("/students/%d", studentId));

        return new StudentInfoPage(studentId);
    }

    protected StudentInfoPage(Integer studentId) {
        this.studentId = studentId;
    }

    public StudentInfoPage verifyPageUrl(String pageUrl) {
        assertThat(url()).endsWith(pageUrl);

        return this;
    }

    public StudentInfoPage verifyInfoMsgPresent(String msg) {
        assertThat($("#infoMsgText").text()).isEqualTo(msg);

        return this;
    }

    public StudentInfoPage verifyFormValues(String firstName, String lastName, String groupName, int... courseIds) {
        $("input#firstName").shouldHave(value(firstName));
        $("input#lastName").shouldHave(value(lastName));
        $$("select#group option").shouldHave(size(1));
        $$("select#group option").get(0).shouldHave(text(groupName));
        // verify student courses
        $$("div#studentCourses input[type='checkbox']").shouldHave(size(courseIds.length));
        for (int courseId : courseIds) {
            $$(format("div#studentCourses input[type='checkbox'][name='course-%d']", courseId)).shouldHave(size(1));
        }

        return this;
    }

    public TimetablesSearchResultPage searchMonthlyTimetablesFor(String targetDate) {
        $(By.name("inlineRadioOptions")).selectRadio("monthly");
        // input search month
        $("input#dateTimePicker").setValue(targetDate);
        // press search button
        $("button#searchTmBtn").click();

        // verify url has changed
        assertThat(url()).endsWith(format("/timetables/student/%d/month?targetDate=%s", studentId, targetDate));
        return new TimetablesSearchResultPage();
    }

    public TimetablesSearchResultPage searchDailyTimetablesFor(String targetDate) {
        $(By.name("inlineRadioOptions")).selectRadio("daily");
        // input search date
        $("input#dateTimePicker").setValue(targetDate);
        // press search button
        $("button#searchTmBtn").click();

        // verify page URL has changed
        assertThat(url()).endsWith(format("/timetables/student/%d/day?targetDate=%s", studentId, targetDate));
        return new TimetablesSearchResultPage();
    }

    public StudentInfoPage searchTimetablesExpectedFailure(String dateType, String targetDate) {
        $(By.name("inlineRadioOptions")).selectRadio(dateType);
        // input search date type
        $("input#dateTimePicker").setValue(targetDate);
        // press search button
        $("button#searchTmBtn").click();

        // verify page URL hasn't changed
        assertThat(url()).endsWith(format("/students/%d", studentId));
        return this;
    }

    public StudentInfoPage verifySearchTimetablesErrorMessage(String msg) {
        $("div#invalidDateInput").shouldBe(visible);
        $("div#invalidDateInput").shouldHave(text(msg));

        return this;
    }

}
