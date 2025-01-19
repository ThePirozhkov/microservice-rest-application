package by.baby.spring.components.mapper;

public interface Mapper<T, V> {

    T mapToDto(V value);

    V mapToEntity(T value);

}
