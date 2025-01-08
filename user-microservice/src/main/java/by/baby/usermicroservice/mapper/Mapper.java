package by.baby.usermicroservice.mapper;

import java.util.Optional;

public interface Mapper<T, V> {

    Optional<T> mapToDto(V value);

    V mapToEntity(T value);

}
