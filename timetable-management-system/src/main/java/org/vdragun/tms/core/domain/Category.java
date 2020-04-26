package org.vdragun.tms.core.domain;

import java.util.Objects;

/**
 * Represents course category
 * 
 * @author Vitaliy Dragun
 *
 */
public class Category {

    private Integer id;
    private String code;
    private String description;

    public Category() {
        this(null);
    }

    public Category(String code) {
        this(code, null);
    }

    public Category(String code, String description) {
        this(null, code, description);
    }

    public Category(Integer id, String code) {
        this(id, code, null);
    }

    public Category(Integer id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Category other = (Category) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", code=" + code + ", description=" + description + "]";
    }

}
