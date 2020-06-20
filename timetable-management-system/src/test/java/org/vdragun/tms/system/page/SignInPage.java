package org.vdragun.tms.system.page;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;

/**
 * Page object for student registration page
 * 
 * @author Vitaliy Dragun
 *
 */
public class SignInPage {

    public static SignInPage open() {
        // Open the home page
        Selenide.open("/");

        return new SignInPage();
    }

    public SignInPage navigateToPage() {
        return new SignInPage();
    }

    protected SignInPage() {
        // navigate to sign in form
        $("a#signin-link").click();
        // verify correct page URL
        assertThat(url()).endsWith("/auth/signin");
    }

    public SignInPage provideInput(String username, String password) {
        $("input#username").setValue(username);
        $("input#password").setValue(password);

        return this;
    }

    public SignInPage submitExpectedSuccess() {
        // press submit button
        $("button#signin-submit").click();
        // verify correct page URL
        assertThat(url()).doesNotEndWith("/auth/signin?error");

        return this;
    }

    public SignInPage submitExpectedFailure() {
        // press submit button
        $("button#signin-submit").click();
        // verify correct page URL
        assertThat(url()).endsWith("/auth/signin?error");

        return this;
    }

    public SignInPage verifyPageUrl(String pageUrl) {
        assertThat(url()).endsWith(pageUrl);
        return this;
    }

    public SignInPage verifyFormErrorMsgPresent(String msg) {
        assertThat($("p.form-errors").text()).isEqualTo(msg);

        return this;
    }

}
