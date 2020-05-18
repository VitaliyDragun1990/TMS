package org.vdragun.tms.dao.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class JPAGroupDao implements GroupDao {

    private static final Logger LOG = LoggerFactory.getLogger(JPAGroupDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize;

    @Override
    public void save(Group group) {
        LOG.debug("Saving new group to the database: {}", group);
        entityManager.persist(group);
    }

    @Override
    public void saveAll(List<Group> groups) {
        LOG.debug("Saving {} new groups to the database", groups.size());
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
    }

    @Override
    public Optional<Group> findById(int groupId) {
        LOG.debug("Searching for group with id={} in the database", groupId);
        return Optional.ofNullable(entityManager.find(Group.class, groupId));
    }

    @Override
    public List<Group> findAll() {
        LOG.debug("Retrieving all groups from the database");
        return entityManager.createQuery("SELECT g FROM  Group g", Group.class).getResultList();
    }

    @Override
    public boolean existsById(Integer groupId) {
        LOG.debug("Checking whether group with id={} exists in the database", groupId);
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT g.id FROM Group g WHERE g.id = ?1",
                Integer.class);
        query.setParameter(1, groupId);
        return !query.getResultList().isEmpty();
    }

}
