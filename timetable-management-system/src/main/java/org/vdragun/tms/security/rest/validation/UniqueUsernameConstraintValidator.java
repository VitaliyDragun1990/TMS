package org.vdragun.tms.security.rest.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.vdragun.tms.security.dao.UserDao;

/**
 * Custom {@link ConstraintValidator} for validation of {@link ExistingRole}
 * constraint
 * 
 * @author Vitaliy Dragun
 *
 */
public class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsername, String> {

    private UserDao userDao;

    public UniqueUsernameConstraintValidator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return userDao.findByUsername(value) == null;
        }
        return true;
    }

}
