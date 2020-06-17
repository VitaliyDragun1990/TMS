package org.vdragun.tms.security.rest.jwt;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.vdragun.tms.security.model.Role;
import org.vdragun.tms.security.model.Status;
import org.vdragun.tms.security.model.User;
import org.vdragun.tms.security.service.AuthenticatedUser;

/**
 * Factory to produce {@link AuthenticatedUser} instances
 * 
 * @author Vitaliy Dragun
 *
 */
public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static AuthenticatedUser create(User user) {
        return new AuthenticatedUser(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword(),
                user.getEmail(),
                user.getStatus().equals(Status.ACTIVE),
                user.getUpdated(),
                mapToGrantedAuthorities(user.getRoles()));
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<Role> userRoles) {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(toList());
    }
}
