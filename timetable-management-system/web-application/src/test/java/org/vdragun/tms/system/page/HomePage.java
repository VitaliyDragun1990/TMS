package org.vdragun.tms.system.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.title;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;

/**
 * Page object for home page
 * 
 * @author Vitaliy Dragun
 *
 */
public class HomePage {

    public static HomePage open() {
        Selenide.open("/");

        return new HomePage();
    }

    protected HomePage() {
    }

    public HomePage verifyTitle(String text) {
        assertThat(title()).isEqualTo(text);

        return this;
    }

    public HomePage verifyHeader(String text) {
        $("#mainContent h1").shouldHave(text(text));

        return this;
    }

}
