package org.jala.university.application.mapper;

public interface Mapper <Entity, DTO> {
    DTO mapTo(Entity entity);
    Entity mapFrom(DTO dto);
}
