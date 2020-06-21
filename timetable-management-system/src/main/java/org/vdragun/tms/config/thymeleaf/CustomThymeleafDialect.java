package org.vdragun.tms.config.thymeleaf;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

/**
 * @author Vitaliy Dragun
 *
 */
public class CustomThymeleafDialect extends AbstractDialect implements IExpressionObjectDialect {

    private final IExpressionObjectFactory customFactory = new ThymeleafUtilExpressionFactory();

    public CustomThymeleafDialect() {
        super("customDialect");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return customFactory;
    }

}
