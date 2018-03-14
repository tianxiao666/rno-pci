package com.hgicreate.rno.lte.common.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contract for a generic dto to entity mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <E> - Entity type parameter.
 * @author chen.c10
 */
public interface EntityMapper<D, E> {

    /**
     * dto to entity
     *
     * @param dto dto
     * @return entity
     */
    E toEntity(D dto);

    /**
     * dto list to entity list
     *
     * @param dtoList dto list
     * @return entity list
     */
    default List<E> toEntity(List<D> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    /**
     * entity to dto
     *
     * @param entity entity
     * @return dto
     */
    D toDto(E entity);

    /**
     * entity list to dto list
     *
     * @param entityList entity list
     * @return dto list
     */
    default List<D> toDto(List<E> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
