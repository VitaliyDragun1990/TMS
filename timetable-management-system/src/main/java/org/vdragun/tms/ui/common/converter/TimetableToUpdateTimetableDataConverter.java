package org.vdragun.tms.ui.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Timetable;

/**
 * Converter to convert {@link Timetable} domain entity into
 * {@link UpdateTimetableData} use case input model
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableToUpdateTimetableDataConverter implements Converter<Timetable, UpdateTimetableData> {

    @Override
    public UpdateTimetableData convert(Timetable timetable) {
        if (timetable != null) {
            return new UpdateTimetableData(
                    timetable.getId(),
                    timetable.getStartTime(),
                    timetable.getDurationInMinutes(),
                    timetable.getClassroom().getId());
        }
        return null;
    }

}
