package org.vdragun.tms.ui.rest.api.v1.mapper;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

/**
 * Common interface for all model mappers
 * 
 * @param <E> entity type
 * @param <R> model type
 * @author Vitaliy Dragun
 *
 */
@FunctionalInterface
public interface RepresentationModelMapper<E, R> {

    R map(E entity);

    default List<R> map(Collection<E> entities) {
        return entities.stream()
                .map(RepresentationModelMapper.this::map)
                .collect(toList());
    }
}
