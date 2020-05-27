package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.List;

/**
 * Responsible for converting business entities into their model representations
 * 
 * @author Vitaliy Dragun
 * 
 *
 */
public interface ModelConverter {

    <E, M> List<M> convertList(List<E> entitites, Class<E> entityClass, Class<M> modelClass);

    <E, M> M convert(E entity, Class<M> modelClass);
}
