package org.vdragun.tms.ui.rest.resource.v1;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;


/**
 * Parent class with common functionality for all application REST resources.
 * 
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractResource {

    public static final String APPLICATION_HAL_JSON = "application/hal+json";

    protected Logger log = LoggerFactory.getLogger(getClass());

    private ModelConverter modelConverter;

    protected String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

    public AbstractResource(ModelConverter modelConverter) {
        this.modelConverter = modelConverter;
    }

    protected <S, T> List<T> convertList(List<S> source, Class<S> sourceClass, Class<T> targetClass) {
        return modelConverter.convertList(source, sourceClass, targetClass);
    }

    protected <S, T> T convert(S source, Class<T> targetClass) {
        return modelConverter.convert(source, targetClass);
    }

}
