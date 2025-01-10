package by.baby.usermicroservice.service;

import java.util.List;
import java.util.Optional;

public interface Service<T, V> {

    List<T> findAll();

    Optional<T> findById(V id);

    T save(T dto);

    T update(T dto, V id);

    void deleteById(V id);

}
