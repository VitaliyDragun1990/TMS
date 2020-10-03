package org.vdragun.tms.ui.rest.api.v1.mapper;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

/**
 * @author Vitaliy Dragun
 *
 */
public class ModelAssembler<E, R extends RepresentationModel<?>, C>
        extends RepresentationModelAssemblerSupport<E, R> {

    private final RepresentationModelMapper<E, R> modelMapper;

    public ModelAssembler(
            Class<C> controllerClass,
            Class<R> representationModelType,
            RepresentationModelMapper<E, R> mapper) {
        super(controllerClass, representationModelType);
        this.modelMapper = mapper;
    }

    @Override
    public R toModel(E entity) {
        return this.modelMapper.map(entity);
    }

}
