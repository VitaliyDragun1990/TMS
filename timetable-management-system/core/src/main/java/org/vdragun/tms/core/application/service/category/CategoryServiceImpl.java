package org.vdragun.tms.core.application.service.category;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;

/**
 * Default implementation of {@link CategoryService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private CategoryDao dao;

    public CategoryServiceImpl(CategoryDao dao) {
        this.dao = dao;
    }

    @Override
    public Category registerNewCategory(CategoryData categoryData) {
        LOG.debug("Registering new category using data: {}", categoryData);

        Category category = new Category(categoryData.getCode(), categoryData.getDescription());
        dao.save(category);

        LOG.debug("New category has been registered: {}", category);
        return category;
    }

    @Override
    @Transactional(readOnly = true)
    public Category findCategoryById(Integer categoryId) {
        LOG.debug("Searching for category with id={}", categoryId);

        return dao.findById(categoryId)
                .orElseThrow(() -> 
                new ResourceNotFoundException(Category.class, "Category with id=%d not found", categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {
        LOG.debug("Retrieving all categories");

        List<Category> result = dao.findAll();

        LOG.debug("Found {} categories", result.size());
        return result;
    }

}
