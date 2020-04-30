package org.vdragun.tms.util.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Group;

@DisplayName("Group Data Generator")
class GroupGeneratorTest {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z]{2}-[0-9]{2}$");

    private GroupGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new GroupGenerator();
    }

    @Test
    void shouldGenerateSpecifiedNumberOfGroups() {
        List<Group> result = generator.generate(5);

        assertThat(result, hasSize(5));
    }

    @Test
    void shouldProvideEachGeneratedGroupWithValidName() {
        List<Group> result = generator.generate(5);

        result.stream()
                .map(g -> g.getName())
                .forEach(name -> assertNameIsValid(name));
    }

    private void assertNameIsValid(String name) {
        assertTrue(NAME_PATTERN.matcher(name).matches(),
                "name should match predefined pattern: " + NAME_PATTERN.toString());
    }
}
