package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.AbstractResource;

/**
 * REST controller that processes timetable registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping("/api/v1/timetables")
public class RegisterTimetableResource extends AbstractResource {

    @Autowired
    private TimetableService timetableService;

    public RegisterTimetableResource(ConversionService conversionService) {
        super(conversionService);
    }

    @PostMapping(produces = "application/hal+json")
    @ResponseStatus(CREATED)
    public TimetableModel registerNewTimetable(@RequestBody CreateTimetableData timetableData) {
        log.trace("Received POST request to register new timetable, data={}, URI={}", timetableData, getRequestUri());

        Timetable timetable = timetableService.registerNewTimetable(timetableData);
        return convert(timetable, TimetableModel.class);
    }

}
