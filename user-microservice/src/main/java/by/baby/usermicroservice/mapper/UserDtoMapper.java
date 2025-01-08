package by.baby.usermicroservice.mapper;

import by.baby.usermicroservice.dto.UserDto;
import by.baby.usermicroservice.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDtoMapper implements Mapper<UserDto, UserEntity> {

    @Override
    public Optional<UserDto> mapToDto(UserEntity value) {
        return Optional.ofNullable(value)
                .map(userEntity -> new UserDto(
                        userEntity.getId(),
                        userEntity.getUsername(),
                        userEntity.getAuthToken(),
                        userEntity.getMoney(),
                        userEntity.getCreatedAt()
                ));
    }

    @Override
    public UserEntity mapToEntity(UserDto value) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(value.getId());
                    userEntity.setUsername(value.getUsername());
                    userEntity.setAuthToken(value.getAuthToken());
                    userEntity.setMoney(value.getMoney());
                    userEntity.setCreatedAt(value.getCreatedAt());
                    return userEntity;
    }
}
