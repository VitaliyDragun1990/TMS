package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableDTO;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes timetable update requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/timetables")
public class UpdateTimetableResource extends AbstractResource {

    @Autowired
    private TimetableService timetableService;

    public UpdateTimetableResource(ConversionService conversionService) {
        super(conversionService);
    }

    @PutMapping("/{timetableId}")
    @ResponseStatus(OK)
    public TimetableDTO updateExistingTimetable(
            @PathVariable Integer timetableId,
            @RequestBody UpdateTimetableData timetableData) {
        log.trace("Received PUT request to update timetable with id={}, data={}, URI={}",
                timetableId, timetableData, getRequestUri());

        Timetable timetable = timetableService.updateExistingTimetable(timetableData);
        return convert(timetable, TimetableDTO.class);
    }

}
