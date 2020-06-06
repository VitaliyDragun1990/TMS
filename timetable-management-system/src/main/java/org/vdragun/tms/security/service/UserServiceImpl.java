package org.vdragun.tms.security.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.security.dao.RoleDao;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.security.model.Role;
import org.vdragun.tms.security.model.Status;
import org.vdragun.tms.security.model.User;

/**
 * Default implementation of the {@link UserService}
 * 
 * @author Vitaliy Dragun
 *
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserDao userDao;
    private RoleDao roleDao;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public User register(User user) {
        Role roleUser = roleDao.findByName("ROLE_USER");
        user.addRole(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(Status.ACTIVE);

        User registeredUser = userDao.save(user);

        LOG.debug("IN register - Successfully registered new user: {}", registeredUser);

        return registeredUser;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAll() {
        List<User> users = userDao.findAll();

        LOG.debug("IN getAll - Found {} users", users.size());
        return users;
    }

    @Override
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);

        if (user == null) {
            LOG.debug("IN findByUsername - No user found by username: {}", username);
        } else {
            LOG.debug("IN findByUsername - Found user: {} by username: {}", user, username);
        }
        return user;
    }

    @Override
    public User findById(Integer id) {
        User user = userDao.findById(id).orElse(null);

        if (user == null) {
            LOG.debug("IN findById - No user found by ID: {}", id);
        } else {
            LOG.debug("IN findById - Found user: {} by ID: {}", user, id);
        }

        return user;
    }

    @Override
    public void delete(Integer id) {
        userDao.deleteById(id);
        LOG.debug("IN delete - User with ID: {} successfully deleted", id);
    }

}
