package org.vdragun.tms.system.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;

/**
 * Page object for student registration page
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentRegistrationPage {

    public static StudentRegistrationPage open() {
        // Open the home page
        Selenide.open("/");
        // navigate to registration form
        $(byCssSelector("[href='#studentSubmenu']")).click();
        $("a#registerStudent").click();
        // verify correct page URL
        assertThat(url()).endsWith("/students/register");

        return new StudentRegistrationPage();
    }

    protected StudentRegistrationPage() {
    }

    public StudentRegistrationPage provideInput(String firstName, String lastName, String enrollmentDate) {
        $("input#firstName").setValue(firstName);
        $("input#lastName").setValue(lastName);
        $("input#enrollmentDate").setValue(enrollmentDate);

        return this;
    }

    public StudentInfoPage submitExpectedSuccess() {
        // press submit button
        $(byCssSelector("[type='submit']")).click();

        String studentId = $("form#studentForm").getAttribute("data-student-id");
        return new StudentInfoPage(Integer.valueOf(studentId));
    }

    public StudentRegistrationPage submitExpectedFailure() {
        // press submit button
        $(byCssSelector("[type='submit']")).click();

        return this;
    }

    public StudentRegistrationPage verifyPageUrl(String pageUrl) {
        assertThat(url()).endsWith(pageUrl);
        return this;
    }

    public StudentRegistrationPage verifyFormErrorMsgPresent(String msg) {
        assertThat($("p.form-errors").text()).isEqualTo(msg);

        return this;
    }

    public StudentRegistrationPage verifyFirstNameErorrs(String... errorMessages) {
        $$("span.first-name-errors ul li").shouldHave(size(errorMessages.length));
        for (String errorMsg : errorMessages) {
            $$("span.first-name-errors ul li").shouldHave(texts(errorMsg));
        }

        return this;
    }

    public StudentRegistrationPage verifyLastNameErorrs(String... errorMessages) {
        $$("span.last-name-errors ul li").shouldHave(size(errorMessages.length));
        for (String errorMsg : errorMessages) {
            $$("span.last-name-errors ul li").shouldHave(texts(errorMsg));
        }

        return this;
    }

    public StudentRegistrationPage verifyEnrollmentDateErorrs(String... errorMessages) {
        $$("span.date-errors ul li").shouldHave(size(errorMessages.length));
        for (String errorMsg : errorMessages) {
            $$("span.date-errors ul li").shouldHave(texts(errorMsg));
        }

        return this;
    }
}
