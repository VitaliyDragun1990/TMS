package org.vdragun.tms.core.application.service;

import java.util.List;

import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.domain.Category;

/**
 * Application entry point to work with {@link Category}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface CategoryService {

    /**
     * Registers new category with given code and description
     * 
     * @param categoryData data to register new category
     * @return registered category instance
     */
    Category registerNewCategory(CategoryData categoryData);

    /**
     * Returns category with given identifier
     * 
     * @param categoryId existing category identifier
     * @return category with given identifier
     * @throws ResourceNotFoundException if no category with given identifier
     */
    Category findCategoryById(Integer categoryId);

    /**
     * Finds all categories available
     * 
     * @return list of all available categories
     */
    List<Category> findAllCategories();
}
