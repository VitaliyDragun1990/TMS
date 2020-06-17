package org.vdragun.tms.security.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.vdragun.tms.security.dao.UserDao;

/**
 * Custom {@link ConstraintValidator} for validation of {@link UniqueEmail}
 * constraint
 * 
 * @author Vitaliy Dragun
 *
 */
public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmail, String> {

    private UserDao userDao;

    public UniqueEmailConstraintValidator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return userDao.findByEmail(value) == null;
        }
        return true;
    }

}
