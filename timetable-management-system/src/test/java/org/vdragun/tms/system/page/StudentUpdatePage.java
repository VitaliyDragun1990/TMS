package org.vdragun.tms.system.page;

import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Page object for student update page
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentUpdatePage {

    private Integer studentId;

    public static StudentUpdatePage openForStudent(Integer studentId) {
        // open student info page
        StudentInfoPage.openForStudent(studentId);
        // click update button
        $("a.updateBtn").click();
        // verify correct page URL
        assertThat(url()).endsWith(format("/students/%d/update", studentId));

        return new StudentUpdatePage(studentId);
    }

    protected StudentUpdatePage(Integer studentId) {
        this.studentId = studentId;
    }

    public StudentUpdatePage provideInput(String firstName, String lastName, String groupId, int... courseIds) {
        $("input#firstName").setValue(firstName);
        $("input#lastName").setValue(lastName);
        $("select#groupId").selectOption(groupId);
        for (int courseId : courseIds) {
            $(byCssSelector(format("input[name='courseIds'][id='%d']", courseId))).setSelected(true);
        }

        return this;
    }

    public StudentInfoPage submit() {
        // press update button
        $("#updateDialogBtn").click();
        $("#confirmUpdateBtn").click();

        return new StudentInfoPage(studentId);
    }
}
