package org.vdragun.tms.dao.data;

import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.TimetableDao;

/**
 * Redefines methods from {@link TimetableDao} interface that should be
 * implemented manually and plugged into {@link SpringDataTimetableDao}
 * 
 * @author Vitaliy Dragun
 *
 */
public interface TimetableDaoFragment {

    /**
     * Updates specified timetable instance.
     */
    void update(Timetable timetable);
}
