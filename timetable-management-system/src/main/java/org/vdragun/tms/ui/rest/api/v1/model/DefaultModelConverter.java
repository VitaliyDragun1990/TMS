package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.List;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Default implementation of {@link ModelConverter} that uses Spring
 * {@link ConversionService} to perform actual conversion
 * 
 * @author Vitaliy Dragun
 *
 */
public class DefaultModelConverter implements ModelConverter {

    private ConversionService conversionService;

    public DefaultModelConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E, M> List<M> convertList(List<E> entitites, Class<E> entityClass, Class<M> modelClass) {
        TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(entityClass));
        TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(modelClass));
        return (List<M>) conversionService.convert(entitites, sourceType, targetType);
    }

    @Override
    public <E, M> M convert(E entity, Class<M> modelClass) {
        return conversionService.convert(entity, modelClass);
    }

}
