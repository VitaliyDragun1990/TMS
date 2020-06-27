package org.vdragun.tms.ui.rest.resource.v1.student;

import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.student.CreateStudentData;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.exception.ApiError;
import org.vdragun.tms.util.Constants.Message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that processes student-related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(
        path = StudentResource.BASE_URL,
        produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
@Validated
@Tag(name = "student", description = "the Student API")
public class StudentResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentResource.class);

    public static final String BASE_URL = "/api/v1/students";

    @Autowired
    private StudentService studentService;

    @Autowired
    private RepresentationModelAssembler<Student, StudentModel> studentModelAssembler;

    @Operation(
            summary = "Find all students available",
            tags = { "student" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = StudentModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = StudentModel.class)))
            })
    @GetMapping
    @ResponseStatus(OK)
    public CollectionModel<StudentModel> getStudents(
            @Parameter(
                    required = false,
                    example = "{\"page\": 1, \"size\": 10}") Pageable pageRequest,
            @Parameter(hidden = true) PagedResourcesAssembler<Student> pagedAssembler) {
        LOG.trace("Received GET request to retrieve students, page request:{}, URI={}",
                pageRequest, getFullRequestUri());

        Page<Student> page = studentService.findStudents(pageRequest);
        Link selfLink = new Link(getFullRequestUri());

        return pagedAssembler.toModel(page, studentModelAssembler, selfLink);
    }

    @Operation(
            summary = "Find student by ID",
            description = "Returns a single student",
            tags = { "student" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = StudentModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentModel.class)),
            })
    @ApiResponse(
            responseCode = "404",
            description = "Student not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{studentId}")
    @ResponseStatus(OK)
    public StudentModel getStudentById(
            @Parameter(description = "Identifier of the student to be obtained. Cannot be null or empty.",
                    example = "1")
            @PathVariable("studentId") @Positive(message = Message.POSITIVE_ID) Integer studentId) {
        LOG.trace("Received GET request to retrieve student with id={}, URI={}", studentId, getFullRequestUri());

        return studentModelAssembler.toModel(studentService.findStudentById(studentId));
    }

    @Operation(
            summary = "Register new student record",
            tags = { "student" })
    @ApiResponse(
            responseCode = "201",
            description = "Student registered",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = StudentModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentModel.class)),
            })
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<StudentModel> registerNewStudent(
            @Parameter(
                    description = "Student to register. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = CreateStudentData.class))
            @RequestBody @Valid CreateStudentData studentData) {
        LOG.trace("Received POST request to register new student, data={}, URI={}", studentData, getFullRequestUri());

        Student student = studentService.registerNewStudent(studentData);
        StudentModel studentModel = studentModelAssembler.toModel(student);

        return ResponseEntity
                .created(studentModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(studentModel);
    }

    @Operation(
            summary = "Update existing student record",
            tags = { "student" })
    @ApiResponse(
            responseCode = "200",
            description = "Student updated",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = StudentModel.class)),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentModel.class)),
            })
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Student record to update not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @PutMapping(path = "/{studentId}")
    @ResponseStatus(OK)
    public StudentModel updateExistingStudent(
            @Parameter(description = "Identifier of the student to be updated. Cannot be null or empty.",
                    example = "1")
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId,
            @Parameter(
                    description = "Data for update. Cannot be null or empty.",
                    required = true,
                    schema = @Schema(implementation = UpdateStudentData.class))
            @RequestBody @Valid UpdateStudentData studentData) {
        LOG.trace(
                "Received PUT request to update student with id={}, data={} URI={}",
                studentId, studentData, getFullRequestUri());

        Student student = studentService.updateExistingStudent(studentData);
        return studentModelAssembler.toModel(student);
    }

    @Operation(
            summary = "Delete existing student record",
            tags = { "student" })
    @ApiResponse(
            responseCode = "200",
            description = "Student deleted")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Student record to delete not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)))
    @DeleteMapping("/{studentId}")
    @ResponseStatus(OK)
    public void deleteStudent(
            @Parameter(description = "Identifier of the student to be deleted. Cannot be null or empty.",
                    example = "1")
            @PathVariable("studentId") @Positive(message = "Positive.id") Integer studentId) {
        LOG.trace("Received DELETE request to delete student with id={}, URI={}", studentId, getFullRequestUri());
        studentService.deleteStudentById(studentId);
    }

}
