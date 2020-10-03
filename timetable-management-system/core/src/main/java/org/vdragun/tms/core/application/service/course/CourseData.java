package org.vdragun.tms.core.application.service.course;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.vdragun.tms.core.application.validation.CourseName;
import org.vdragun.tms.core.application.validation.LatinSentence;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains necessary data to create new course
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information necessary to register new course")
public class CourseData {

    @NotNull
    @CourseName
    @Schema(
            description = "Name of the course",
            pattern = "^[A-Z]{1}[A-Za-z ]{1,}[a-z]{1}$",
            minLength = 5,
            maxLength = 50,
            example = "Advanced English",
            required = true)
    private String name;

    @LatinSentence
    @Schema(
            description = "Optional description of the course",
            pattern = "^[A-Za-z0-9,.-:!?;]+(\\s+[A-Za-z0-9,.-:!?;]+)*$",
            example = "Updated Advanced English course",
            required = false)
    private String description;

    @NotNull
    @Positive
    @Schema(
            description = "Identfier of the category course belongs to",
            example = "1",
            required = true)
    private Integer categoryId;

    @NotNull
    @Positive
    @Schema(
            description = "Identifier of the teacher assigned to the course",
            example = "1",
            required = true)
    private Integer teacherId;

    public CourseData() {
    }

    public CourseData(String name, String description, Integer categoryId, Integer teacherId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "CourseData [name=" + name + ", description=" + description + ", categoryId=" + categoryId
                + ", teacherId=" + teacherId + "]";
    }

}
