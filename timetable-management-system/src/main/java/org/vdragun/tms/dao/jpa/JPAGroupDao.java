package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.GroupDao;

/**
 * Implementation of {@link GroupDao} using JPA
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JPAGroupDao extends BaseJPADao implements GroupDao {

    public JPAGroupDao(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    public void save(Group group) {
        log.debug("Saving new group to the database: {}", group);
        execute(entityManager -> entityManager.persist(group));
    }

    @Override
    public void saveAll(List<Group> groups) {
        log.debug("Saving {} new groups to the database", groups.size());
        int batchSize = getBatchSize();
        execute(entityManager -> {
            for (int i = 0; i < groups.size(); i++) {
                // saves group instance into active persistent context (but not in the database)
                entityManager.persist(groups.get(i));
                if (i % batchSize == 0 || i == groups.size() - 1) {
                    // flush changes to the database (actually save groups into the database)
                    entityManager.flush();
                    // clear persistence context from all persisted entities
                    entityManager.clear();
                }
            }
        });
    }

    @Override
    public Optional<Group> findById(int groupId) {
        log.debug("Searching for group with id={} in the database", groupId);
        return query(entityManager -> Optional.ofNullable(entityManager.find(Group.class, groupId)));
    }

    @Override
    public List<Group> findAll() {
        log.debug("Retrieving all groups from the database");
        return query(entitymanager -> entitymanager.createQuery("SELECT g FROM  Group g", Group.class).getResultList());
    }

    @Override
    public boolean existsById(Integer groupId) {
        log.debug("Checking whether group with id={} exists in the database", groupId);
        return query(entityManager -> {
            TypedQuery<Integer> query = entityManager.createQuery(
                    "SELECT g.id FROM Group g WHERE g.id = ?1",
                    Integer.class);
            query.setParameter(1, groupId);
            return !query.getResultList().isEmpty();
        });
    }

}
