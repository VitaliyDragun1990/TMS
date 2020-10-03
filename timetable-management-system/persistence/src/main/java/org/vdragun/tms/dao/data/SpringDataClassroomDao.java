package org.vdragun.tms.dao.data;

import org.springframework.data.repository.RepositoryDefinition;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;

/**
 * Implementation of {@link ClassroomDao} using Spring Data
 * 
 * @author Vitaliy Dragun
 *
 */
@RepositoryDefinition(domainClass = Classroom.class, idClass = Integer.class)
public interface SpringDataClassroomDao extends ClassroomDao {

}
