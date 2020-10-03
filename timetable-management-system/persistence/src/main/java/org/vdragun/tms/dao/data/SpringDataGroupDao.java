package org.vdragun.tms.dao.data;

import org.springframework.data.repository.RepositoryDefinition;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.GroupDao;

/**
 * Implementation of {@link GroupDao} using Spring Data
 *
 * @author Vitaliy Dragun
 */
@RepositoryDefinition(domainClass = Group.class, idClass = Integer.class)
public interface SpringDataGroupDao extends GroupDao {

}
