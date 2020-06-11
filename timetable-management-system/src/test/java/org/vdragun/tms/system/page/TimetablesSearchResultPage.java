package org.vdragun.tms.system.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page object for timetables search result page
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetablesSearchResultPage {

    protected TimetablesSearchResultPage() {
    }

    public TimetablesSearchResultPage verifyInfoMsg(String msg) {
        $("h4#headerMsg").shouldHave(text(msg));

        return this;
    }

    public TimetablesSearchResultPage verifyTimetablesCount(int expectedCount) {
        $$("table#timetablesTable tbody tr").shouldHave(size(expectedCount));

        return this;
    }
}
