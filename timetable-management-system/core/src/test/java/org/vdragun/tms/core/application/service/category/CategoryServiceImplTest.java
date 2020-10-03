package org.vdragun.tms.core.application.service.category;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category service")
class CategoryServiceImplTest {

    private static final Integer ID = 1;

    private static final String CODE_BIO = "BIO";

    private static final String CODE_ART = "ART";

    private static final String DESC_BIO = "Biology";

    private static final String DESC_ART = "Art";

    @Captor
    private ArgumentCaptor<Category> captor;

    @Mock
    private CategoryDao daoMock;

    private CategoryService service;

    @BeforeEach
    void setUp() {
        service = new CategoryServiceImpl(daoMock);
    }

    @Test
    void shouldRegisterNewCategory() {
        CategoryData categoryData = new CategoryData(CODE_BIO, DESC_BIO);

        service.registerNewCategory(categoryData);

        verify(daoMock, times(1)).save(captor.capture());
        Category savedCategory = captor.getValue();
        assertThat(savedCategory.getCode(), equalTo(CODE_BIO));
        assertThat(savedCategory.getDescription(), equalTo(DESC_BIO));
    }

    @Test
    void shouldThrowExceptionIfNoCategoryWithGivenIdentifier() {
        when(daoMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findCategoryById(ID));
    }

    @Test
    void shouldFindCategoryById() {
        Category expected = new Category(ID, CODE_BIO, CODE_BIO);
        when(daoMock.findById(ArgumentMatchers.eq(ID))).thenReturn(Optional.of(expected));

        Category result = service.findCategoryById(ID);

        assertThat(result, equalTo(expected));
    }

    @Test
    void shouldFindAllCategories() {
        Category bio = new Category(ID, CODE_BIO, CODE_BIO);
        Category art = new Category(ID + 1, CODE_ART, DESC_ART);
        when(daoMock.findAll()).thenReturn(Arrays.asList(bio, art));

        List<Category> result = service.findAllCategories();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(art, bio));
    }

}
