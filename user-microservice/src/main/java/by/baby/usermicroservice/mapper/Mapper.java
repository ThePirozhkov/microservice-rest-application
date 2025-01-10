package by.baby.usermicroservice.mapper;

public interface Mapper<T, V> {

    T mapToDto(V value);

    V mapToEntity(T value);

}
