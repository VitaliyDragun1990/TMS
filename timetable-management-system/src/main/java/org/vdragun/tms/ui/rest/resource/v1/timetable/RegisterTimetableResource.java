package org.vdragun.tms.ui.rest.resource.v1.timetable;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.timetable.CreateTimetableData;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
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

    public RegisterTimetableResource(ModelConverter converter) {
        super(converter);
    }

    @PostMapping(produces = "application/hal+json")
    public ResponseEntity<TimetableModel> registerNewTimetable(@RequestBody @Valid CreateTimetableData timetableData) {
        log.trace("Received POST request to register new timetable, data={}, URI={}", timetableData, getRequestUri());

        Timetable timetable = timetableService.registerNewTimetable(timetableData);
        TimetableModel timetableModel = convert(timetable, TimetableModel.class);

        return ResponseEntity
                .created(timetableModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(timetableModel);
    }

}
