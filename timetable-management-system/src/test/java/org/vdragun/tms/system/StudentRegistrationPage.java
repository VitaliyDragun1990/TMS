package org.vdragun.tms.system;

import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;

/**
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
        // verify correct url
        assertThat(url()).endsWith("/students/register");

        return new StudentRegistrationPage();
    }

    public StudentRegistrationPage provideInput(String firstName, String lastName, String enrollmentDate) {
        $("input#firstName").setValue(firstName);
        $("input#lastName").setValue(lastName);
        $("input#enrollmentDate").setValue(enrollmentDate);

        return this;
    }

    public StudentRegistrationPage submit() {
        // press submit button
        $(byCssSelector("[type='submit']")).click();

        return this;
    }
}
