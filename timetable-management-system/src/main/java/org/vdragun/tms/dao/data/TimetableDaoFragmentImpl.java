package org.vdragun.tms.dao.data;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vdragun.tms.core.domain.Timetable;

/**
 * Implementation of {@link TimetableDaoFragment} using JPA
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableDaoFragmentImpl implements TimetableDaoFragment {

    private static final Logger LOG = LoggerFactory.getLogger(TimetableDaoFragmentImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void update(Timetable timetable) {
        LOG.debug("Updating timetable with id={} with data={} in the database", timetable.getId(), timetable);
        // In order to throw exception if entity to update does not exist
        entityManager.unwrap(Session.class).update(timetable);
    }

}
