package by.baby.spring.components.service;

import by.baby.dto.Dto;

import java.util.List;
import java.util.Optional;

public interface Service<T extends Dto, V> {

    List<T> findAll();

    Optional<T> findById(V id);

    Optional<T> save(T dto);

    Optional<T> update(T dto, V id);

    boolean deleteById(V id);

}
