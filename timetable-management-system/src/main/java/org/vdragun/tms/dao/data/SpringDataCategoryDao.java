package org.vdragun.tms.dao.data;

import org.springframework.data.repository.RepositoryDefinition;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;

/**
 * Implementation of {@link CategoryDao} using Spring Data
 * 
 * @author Vitaliy Dragun
 *
 */
@RepositoryDefinition(domainClass = Category.class, idClass = Integer.class)
public interface SpringDataCategoryDao extends CategoryDao {

}
