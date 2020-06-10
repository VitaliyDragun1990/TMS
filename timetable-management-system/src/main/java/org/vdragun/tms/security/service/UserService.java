package org.vdragun.tms.security.service;

import java.util.List;

import org.vdragun.tms.security.model.User;

/**
 * Application entry point to work with {@link User}s
 * 
 * @author Vitaliy Dragun
 *
 */
public interface UserService {

    User register(User user);

    List<User> getAll();

    User findByUsername(String username);

    User findById(Integer id);

    void delete(Integer id);
}
