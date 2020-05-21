package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.http.HttpStatus.OK;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes timetable update requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/timetables")
@Validated
public class UpdateTimetableResource extends AbstractResource {

    @Autowired
    private TimetableService timetableService;

    public UpdateTimetableResource(ModelConverter converter) {
        super(converter);
    }

    @PutMapping(path = "/{timetableId}", produces = "application/hal+json")
    @ResponseStatus(OK)
    public TimetableModel updateExistingTimetable(
            @PathVariable @Positive(message = "Positive.id") Integer timetableId,
            @RequestBody @Valid UpdateTimetableData timetableData) {
        log.trace("Received PUT request to update timetable with id={}, data={}, URI={}",
                timetableId, timetableData, getRequestUri());

        Timetable timetable = timetableService.updateExistingTimetable(timetableData);
        return convert(timetable, TimetableModel.class);
    }

}
