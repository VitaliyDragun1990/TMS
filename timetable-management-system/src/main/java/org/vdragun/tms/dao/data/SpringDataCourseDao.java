package org.vdragun.tms.dao.data;

import org.springframework.data.repository.RepositoryDefinition;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.dao.CourseDao;

/**
 * Implementation of {@link CourseDao} using Spring Data
 * 
 * @author Vitaliy Dragun
 *
 */
@RepositoryDefinition(domainClass = Course.class, idClass = Integer.class)
public interface SpringDataCourseDao extends CourseDao {

}
