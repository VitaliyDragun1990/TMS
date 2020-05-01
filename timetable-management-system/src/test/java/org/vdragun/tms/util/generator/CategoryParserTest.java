package org.vdragun.tms.util.generator;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Category;

@DisplayName("Category Parser")
public class CategoryParserTest {
    private static final List<String> CATEGORY_DATA = asList("ART=Art", "BIO=Biology", "ENG=English");

    private CategoryParser parser;

    @BeforeEach
    void setUp() {
        parser = new CategoryParser();
    }

    @Test
    void shouldParseRighNumberOfCategoryInstances() {
        List<Category> result = parser.parse(CATEGORY_DATA);

        assertThat(result, hasSize(CATEGORY_DATA.size()));
    }

    @Test
    void shouldCorrectlyParseCategoryData() {
        List<Category> result = parser.parse(CATEGORY_DATA);

        assertThat(result, containsInAnyOrder(
                category("ART", "Art"),
                category("BIO", "Biology"),
                category("ENG", "English")));
    }

    @Test
    void shouldThrowExceptionIfSpecifiedDataInWrongFormat() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(asList("ART-Art")));
    }

    private Matcher<? super Category> category(String code, String desc) {
        return allOf(
                hasProperty("code", equalTo(code)),
                hasProperty("description", equalTo(desc)));
    }

}
