package org.vdragun.tms.acceptance.stories;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import java.net.URL;
import java.util.List;

import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.StoryFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.vdragun.tms.EmbeddedDataSourceConfig;
import org.vdragun.tms.acceptance.steps.SearchStudentMonthlyTimetablesSteps;
import org.vdragun.tms.core.application.service.timetable.TimetableServiceImpl;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({
        TimetableServiceImpl.class,
        EmbeddedDataSourceConfig.class,
        SearchStudentMonthlyTimetablesSteps.class // steps definition needs explicit import
})
@TestPropertySource(properties = {
        "db.clear_script=/sql/clear_database.sql",
        "db.course_three_timetables_script=/sql/stories/three_course_timetables.sql",
        "db.student_course_reg_script=/sql/stories/student_course_registration.sql"
})
@DisplayName("Application Story Runner")
public class TmsStoryRunner {

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    void runClasspathLoadedStories() {
        Embedder embedder = new TmsEmbedder(applicationContext);
        List<String> storyPaths = findStoriesFromClasspath();
        try {
            embedder.runStoriesAsPaths(storyPaths);
        } finally {
            embedder.generateCrossReference();
            embedder.generateSurefireReport();
        }
    }


    private List<String> findStoriesFromClasspath() {
        URL location = codeLocationFromClass(this.getClass());
        return new StoryFinder().findPaths(location, "**/*.story", "**/*excluded*.story");
    }

}
