package org.vdragun.tms.ui.web.controller.timetable;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.classroom.ClassroomService;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.View;

/**
 * Processes timetable update requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/timetables")
public class UpdateTimetableController extends AbstractController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private ConversionService conversionService;

    @ModelAttribute(Attribute.CLASSROOMS)
    List<Classroom> allClassrooms() {
        return classroomService.findAllClassrooms();
    }

    @GetMapping("/{timetableId}/update")
    public String showUpdateForm(@PathVariable("timetableId") Integer timetableId, Model model) {
        log.trace("Received GET request to show timetable update form for timetable with id={}, URI={}",
                timetableId, getRequestUri());
        model.addAttribute(
                Attribute.TIMETABLE,
                conversionService.convert(timetableService.findTimetableById(timetableId), UpdateTimetableData.class));

        return View.TIMETABLE_UPDATE_FORM;
    }

    @PostMapping("/{timetableId}")
    public String updateTimetable(
            @PathVariable Integer timetableId,
            @Valid @ModelAttribute("timetable") UpdateTimetableData timetableData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.trace("Received POST request to update timetable with id={}, data={}, URI={}",
                timetableId, timetableData, getRequestUri());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.trace("Validation error: {}", error));
            model.addAttribute(Attribute.VALIDATED, true);

            return View.TIMETABLE_UPDATE_FORM;
        }

        timetableService.updateExistingTimetable(timetableData);
        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.TIMETABLE_UPDATE_SUCCESS));

        return redirectTo("/timetables/" + timetableId);
    }
}
