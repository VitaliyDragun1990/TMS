package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Teacher;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object with essential information for particular
 * {@link Teacher} domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "teachers", itemRelation = "teacher")
@Schema(description = "DTO containing essential information about particular teacher")
public class TeacherModel extends RepresentationModel<TeacherModel> {

    @Schema(description = "Unique identifier of the teacher", example = "1")
    private Integer id;

    @Schema(description = "Teacher first name", example = "John")
    private String firstName;

    @Schema(description = "Teacher last name", example = "Smith")
    private String lastName;

    @Schema(
            description = "Teacher title",
            example = "INSTRUCTOR",
            allowableValues = { "PROFESSOR", "ASSOCIATE_PROFESSOR", "INSTRUCTOR" })
    private String title;

    @Schema(description = "Date when particular teacher was hired", example = "24/05/2020")
    private String dateHired;

    @ArraySchema(schema = @Schema(implementation = CourseModel.class))
    private List<CourseModel> courses;

    public TeacherModel() {
    }

    public TeacherModel(
            Integer id,
            String firstName,
            String lastName,
            String title,
            String dateHired,
            List<CourseModel> courses) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.dateHired = dateHired;
        this.courses = courses;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateHired() {
        return dateHired;
    }

    public void setDateHired(String dateHired) {
        this.dateHired = dateHired;
    }

    public List<CourseModel> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseModel> courses) {
        this.courses = courses;
    }

    @Schema(hidden = true)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TeacherModel other = (TeacherModel) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "TeacherDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", title=" + title
                + ", dateHired=" + dateHired + ", courses=" + courses + "]";
    }

}
