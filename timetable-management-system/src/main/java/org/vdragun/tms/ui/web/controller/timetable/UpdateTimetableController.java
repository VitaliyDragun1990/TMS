package org.vdragun.tms.ui.web.controller.timetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.TimetableService;
import org.vdragun.tms.core.application.service.UpdateTimetableData;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;

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
