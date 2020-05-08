package org.vdragun.tms.ui.web.controller.timetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.ClassroomService;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.application.service.UpdateTimetableData;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

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

    @GetMapping("/{timetableId}/update")
    public String showUpdateForm(@PathVariable("timetableId") Integer timetableId, Model model) {
        log.trace("Received GET request to show timetable update form for timetable with id={}, URI={}",
                timetableId, getRequestUri());
        model.addAttribute(Attribute.TIMETABLE, timetableService.findTimetableById(timetableId));
        model.addAttribute(Attribute.CLASSROOMS, classroomService.findAllClassrooms());

        return Page.TIMETABLE_UPDATE_FORM;
    }

    @PostMapping("/{timetableId}")
    public String updateTimetable(
            @PathVariable Integer timetableId,
            @ModelAttribute UpdateTimetableData timetableData,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.trace("Received POST request to update timetable with id={}, data={}, URI={}",
                timetableId, timetableData, getRequestUri());
        timetableService.updateExistingTimetable(timetableData);

        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.TIMETABLE_UPDATE_SUCCESS));

        return redirectTo("timetables/" + timetableId);
    }
}
