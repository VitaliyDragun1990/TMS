package org.vdragun.tms.config.thymeleaf;

import java.util.Collections;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

/**
 * @author Vitaliy Dragun
 *
 */
public class ThymeleafUtilExpressionFactory implements IExpressionObjectFactory {

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return Collections.singleton("myutil");
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        return new ThymeleafUtil();
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return true;
    }

}
