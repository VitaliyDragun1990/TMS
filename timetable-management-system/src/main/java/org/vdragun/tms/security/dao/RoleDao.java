package org.vdragun.tms.security.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vdragun.tms.security.model.Role;

/**
 * Defines CRUD operations to access course {@link Role} objects in the
 * persistent storage
 * 
 * @author Vitaliy Dragun
 *
 */
public interface RoleDao extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);
}
