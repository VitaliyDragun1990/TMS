package org.vdragun.tms.core.application.service;

/**
 * Contains necessary data to create new category
 * 
 * @author Vitaliy Dragun
 *
 */
public class CategoryData {

    private String code;
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
