package org.vdragun.tms.security.rest.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.vdragun.tms.security.dao.RoleDao;

/**
 * Custom {@link ConstraintValidator} for validation of {@link ExistingRole}
 * constraint
 * 
 * @author Vitaliy Dragun
 *
 */
public class ValidRoleConstraintValidator implements ConstraintValidator<ValidRole, String> {

    private static final Pattern ROLE_NAME_PATTERN = Pattern.compile("^ROLE_[A-Z]{4,}$");

    private RoleDao roleDao;

    public ValidRoleConstraintValidator(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return ROLE_NAME_PATTERN.matcher(value).matches() && roleDao.findByName(value).isPresent();
        }
        return true;
    }

}
