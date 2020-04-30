package org.vdragun.tms.core.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.CategoryData;
import org.vdragun.tms.core.application.service.CategoryService;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;

/**
 * Default implementation of {@link CategoryService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryDao dao;

    public CategoryServiceImpl(CategoryDao dao) {
        this.dao = dao;
    }

    @Override
    public Category registerNewCategory(CategoryData categoryData) {
        Category category = new Category(categoryData.getCode(), categoryData.getDescription());
        dao.save(category);
        return category;
    }

    @Override
    public Category findCategoryById(Integer categoryId) {
        return dao.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id=%d not found", categoryId));
    }

    @Override
    public List<Category> findAllCategories() {
        return dao.findAll();
    }

}
