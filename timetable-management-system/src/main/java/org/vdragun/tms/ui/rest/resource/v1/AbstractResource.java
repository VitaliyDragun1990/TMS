package org.vdragun.tms.ui.rest.resource.v1;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


/**
 * Parent class with common functionality for all application REST resources.
 * 
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractResource {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private ConversionService conversionService;

    public AbstractResource(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    protected String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

    @SuppressWarnings("unchecked")
    protected <S, T> List<T> convertList(List<S> source, Class<S> sourceClass, Class<T> targetClass) {
        TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));
        return (List<T>) conversionService.convert(source, sourceType, targetType);
    }

    protected <S, T> T convert(S source, Class<T> targetClass) {
        return conversionService.convert(source, targetClass);
    }

}
