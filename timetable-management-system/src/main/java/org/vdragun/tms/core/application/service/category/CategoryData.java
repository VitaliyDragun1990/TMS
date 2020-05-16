package org.vdragun.tms.core.application.service.category;

import javax.validation.constraints.NotBlank;

import org.vdragun.tms.core.application.validation.CategoryCode;
import org.vdragun.tms.core.application.validation.LatinCharacters;

/**
 * Contains necessary data to create new category
 * 
 * @author Vitaliy Dragun
 *
 */
public class CategoryData {

    @NotBlank
    @CategoryCode
    private String code;

    @LatinCharacters
    private String description;

    public CategoryData() {
    }

    public CategoryData(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CategoryData [code=" + code + ", description=" + description + "]";
    }

}
