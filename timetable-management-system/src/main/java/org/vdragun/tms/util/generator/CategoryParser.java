package org.vdragun.tms.util.generator;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.vdragun.tms.core.domain.Category;

/**
 * Parses provided data in format 'code=description' into {@link Category}
 * instances
 * 
 * @author Vitaliy Dragun
 *
 */
public class CategoryParser {

    /**
     * Parses specified data, which should be in format 'code=description' into list
     * of categories
     */
    public List<Category> parse(List<String> categoryData) {
        return categoryData.stream()
                .map(this::parseToCategory)
                .collect(toList());
    }

    private Category parseToCategory(String data) {
        String[] elements = data.split("=");
        if (elements.length != 2) {
            throw new IllegalArgumentException("Data to parse category from has invalid format:" + data);
        }
        return new Category(elements[0], elements[1]);
    }
}
