package org.vdragun.tms.dao;

import java.util.List;
import java.util.Optional;

import org.vdragun.tms.core.domain.Category;

/**
 * Defines CRUD operations to access course {@link Category} objects in the
 * persistent storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface CategoryDao {


    /**
     * Saves specified category instance. Saved object receives unique identifier.
     */
    void save(Category category);

    /**
     * Saves all specified category instances. Each saved object receives unique
     * identifier.
     */
    void saveAll(List<Category> groups);

    /**
     * Returns category with specified identifier if any
     */
    Optional<Category> findById(Integer categoryId);

    /**
     * Finds all available categories
     */
    List<Category> findAll();
}
