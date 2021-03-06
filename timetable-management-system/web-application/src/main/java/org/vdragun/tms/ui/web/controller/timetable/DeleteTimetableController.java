package org.vdragun.tms.ui.web.controller.timetable;

import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.ui.web.controller.AbstractController;
import org.vdragun.tms.config.WebConstants.Attribute;
import org.vdragun.tms.config.Constants.Message;

/**
 * Processes timetable delete requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/timetables")
public class DeleteTimetableController extends AbstractController {

    @Autowired
    private TimetableService timetableService;

    @PostMapping("/delete")
    public String deleteTimetable(
            @RequestParam("id") Integer timetableId,
            RedirectAttributes redirectAttributes) {
        log.trace("Received POST reuqest to delete timetable with id={}, URI={}", timetableId, getFullRequestUri());
        timetableService.deleteTimetableById(timetableId);

        redirectAttributes.addFlashAttribute(
                Attribute.INFO_MESSAGE,
                getMessage(Message.TIMETABLE_DELETE_SUCCESS, timetableId));

        return redirectTo("/timetables");
    }
}
