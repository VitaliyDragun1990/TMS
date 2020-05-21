package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.http.HttpStatus.OK;

import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes timetable delete requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/timetables")
@Validated
public class DeleteTimetableResource extends AbstractResource {

    @Autowired
    private TimetableService timetableService;

    public DeleteTimetableResource(ModelConverter converter) {
        super(converter);
    }

    @DeleteMapping("/{timetableId}")
    @ResponseStatus(OK)
    public void deleteTimetable(@PathVariable("timetableId") @Positive(message = "Positive.id") Integer timetableId) {
        log.trace("Received POST reuqest to delete timetable with id={}, URI={}", timetableId, getRequestUri());
        timetableService.deleteTimetableById(timetableId);
    }

}
