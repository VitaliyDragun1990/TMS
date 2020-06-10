package org.vdragun.tms.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vdragun.tms.security.model.User;

/**
 * Defines CRUD operations to access course {@link User} objects in the
 * persistent storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface UserDao extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}
