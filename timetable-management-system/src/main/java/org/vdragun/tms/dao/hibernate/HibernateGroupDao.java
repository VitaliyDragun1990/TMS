package org.vdragun.tms.dao.hibernate;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.GroupDao;

/**
 * Implementation of {@link GroupDao} using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class HibernateGroupDao extends BaseHibernateDao implements GroupDao {

    protected HibernateGroupDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void save(Group group) {
        log.debug("Saving new group to the database: {}", group);
        execute(session -> session.save(group));
    }

    @Override
    public void saveAll(List<Group> groups) {
        log.debug("Saving {} new groups to the database", groups.size());
        int batchSize = getBatchSize();
        execute(session -> {
            for (int i = 0; i < groups.size(); i++) {
                // saves group instance into active session (but not in the database)
                session.persist(groups.get(i));
                if (i % batchSize == 0 || i == groups.size() - 1) {
                    // flush changes to the database (actually save groups into the database)
                    session.flush();
                    // clear session from all persisted entities
                    session.clear();
                }
            }
        });
    }

    @Override
    public Optional<Group> findById(int groupId) {
        log.debug("Searching for group with id={} in the database", groupId);
        return query(session -> Optional.ofNullable(session.get(Group.class, groupId)));
    }

    @Override
    public List<Group> findAll() {
        log.debug("Retrieving all groups from the database");
        return query(session -> session.createQuery("SELECT g FROM  Group g", Group.class).list());
    }

    @Override
    public boolean existsById(Integer groupId) {
        log.debug("Checking whether group with id={} exists in the database", groupId);
        return query(session -> Optional.ofNullable(session.get(Group.class, groupId)).isPresent());
    }

}
