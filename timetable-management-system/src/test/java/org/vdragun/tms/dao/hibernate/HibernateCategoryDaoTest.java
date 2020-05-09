package org.vdragun.tms.dao.hibernate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.vdragun.tms.config.HibernateDaoConfig;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;
import org.vdragun.tms.dao.jdbc.DBTestConfig;
import org.vdragun.tms.dao.jdbc.JdbcTestHelper;

@SpringJUnitConfig(classes = { HibernateDaoConfig.class, DBTestConfig.class })
@Sql(scripts = { "/sql/db_schema_seq.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Hibernate Category DAO")
public class HibernateCategoryDaoTest {

    private static final String CODE_ART = "ART";
    private static final String CODE_BIO = "BIO";
    private static final String DESC_ART = "Art";
    private static final String DESC_BIO = "Biology";

    @Autowired
    private CategoryDao dao;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldReturnEmptyResultIfNoCategoryWithGivenIdInDatabase() {
        Optional<Category> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindCategoryById() throws SQLException {
        Category expected = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);

        Optional<Category> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewCategoryToDatabase() throws SQLException {
        Category category = new Category(CODE_ART, DESC_ART);

        dao.save(category);

        assertCategoriesInDatabase(category);
    }

    @Test
    void shouldSaveSeveralNewCategoriesToDatabase() throws SQLException {
        Category art = new Category(CODE_ART, DESC_ART);
        Category bio = new Category(CODE_BIO, DESC_BIO);

        dao.saveAll(Arrays.asList(art, bio));

        assertCategoriesInDatabase(art, bio);
    }

    @Test
    void shouldReturnEmptyListIfNoCategoriesInDatabase() {
        List<Category> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFildAllCategoryInstancesInDatabase() throws SQLException {
        Category art = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Category bio = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIO);

        List<Category> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(art, bio));
    }

    @Test
    void shouldReturnTrueIfCategoryWithGivenIdentifierExists() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);

        boolean result = dao.existsById(category.getId());

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseIfCategoryWithGivenIdentifierNotExist() throws SQLException {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    private void assertCategoriesInDatabase(Category... expected) throws SQLException {
        Arrays.stream(expected)
                .forEach(category -> assertThat("category should have id", category.getId(), is(not(nullValue()))));

        List<Category> result = jdbcHelper.findAllCategoriesInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
