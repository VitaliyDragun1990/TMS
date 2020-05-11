package org.vdragun.tms.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents course category
 * 
 * @author Vitaliy Dragun
 *
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categoryGen")
    @SequenceGenerator(name = "categoryGen", sequenceName = "categories_category_id_seq", allocationSize = 1)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_code")
    private String code;

    @Column(name = "category_description")
    private String description;

    protected Category() {
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
        return 2021;
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
        return id != null && id.equals(other.getId());
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", code=" + code + ", description=" + description + "]";
    }

}
