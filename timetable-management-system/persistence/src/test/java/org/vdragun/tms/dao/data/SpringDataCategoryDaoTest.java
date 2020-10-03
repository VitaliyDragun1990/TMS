package org.vdragun.tms.dao.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.vdragun.tms.config.DBTestHelper;
import org.vdragun.tms.config.DaoTestConfig;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;

@DataJpaTest
@Import({DaoTestConfig.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Spring Data Category DAO")
public class SpringDataCategoryDaoTest {

    private static final String CODE_ART = "ART";

    private static final String CODE_BIO = "BIO";

    private static final String CODE_HIS = "HIS";

    private static final String DESC_ART = "Art";

    private static final String DESC_BIO = "Biology";

    @Autowired
    private CategoryDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoCategoryWithGivenIdInDatabase() {
        Optional<Category> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/category_data.sql"})
    void shouldFindCategoryById() {
        Category expected = dbHelper.findCategoryByCodeInDatabase(CODE_ART);

        Optional<Category> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = {"/sql/clear_database.sql"})
    void shouldSaveNewCategoryToDatabase() {
        Category category = new Category(CODE_ART, DESC_ART);

        dao.save(category);
        dbHelper.flushChangesToDatabase();

        assertCategoriesInDatabase(category);
    }

    @Test
    @Sql(scripts = {"/sql/clear_database.sql"})
    void shouldSaveSeveralNewCategoriesToDatabase() {
        Category art = new Category(CODE_ART, DESC_ART);
        Category bio = new Category(CODE_BIO, DESC_BIO);

        dao.saveAll(Arrays.asList(art, bio));
        dbHelper.flushChangesToDatabase();

        assertCategoriesInDatabase(art, bio);
    }

    @Test
    @Sql(scripts = {"/sql/clear_database.sql"})
    void shouldReturnEmptyListIfNoCategoriesInDatabase() {
        List<Category> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/category_data.sql"})
    void shouldFildAllCategoryInstancesInDatabase() {
        List<Category> result = dao.findAll();

        assertCategoriesWithCodes(result, CODE_ART, CODE_BIO, CODE_HIS);
    }

    @Test
    @Sql(scripts = {"/sql/clear_database.sql", "/sql/category_data.sql"})
    void shouldReturnTrueIfCategoryWithGivenIdentifierExists() {
        Category category = dbHelper.findCategoryByCodeInDatabase(CODE_ART);

        boolean result = dao.existsById(category.getId());

        assertTrue(result);
    }

    @Test
    @Sql(scripts = {"/sql/clear_database.sql"})
    void shouldReturnFalseIfCategoryWithGivenIdentifierNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    private void assertCategoriesWithCodes(List<Category> result, String... codes) {
        assertThat(result, hasSize(codes.length));
        for (String code : codes) {
            assertThat(result, hasItem(hasProperty("code", equalTo(code))));
        }
    }

    private void assertCategoriesInDatabase(Category... expected) {
        Arrays.stream(expected)
                .forEach(category -> assertThat("category should have id", category.getId(), is(not(nullValue()))));

        List<Category> result = dbHelper.findAllCategoriesInDatabase();
        assertThat(result, hasItems(expected));
    }
}
