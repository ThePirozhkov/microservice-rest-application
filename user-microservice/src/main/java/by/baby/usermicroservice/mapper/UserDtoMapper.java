package by.baby.usermicroservice.mapper;

import by.baby.persistence.entity.UserEntity;
import by.baby.usermicroservice.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDtoMapper implements Mapper<UserDto, UserEntity> {

    @Override
    public UserDto mapToDto(UserEntity value) {
        return Optional.ofNullable(value)
                .map(userEntity -> new UserDto(
                        userEntity.getId(),
                        userEntity.getUsername(),
                        userEntity.getAuthToken(),
                        userEntity.getMoney(),
                        userEntity.getCreatedAt()
                ))
                .orElseThrow(() -> new RuntimeException("Can't map to UserDto"));
    }

    @Override
    public UserEntity mapToEntity(UserDto value) {
        if (value == null) {
            throw new RuntimeException("Can't map to UserEntity");
        }
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(value.getId());
                    userEntity.setUsername(value.getUsername());
                    userEntity.setAuthToken(value.getAuthToken());
                    userEntity.setMoney(value.getMoney());
                    userEntity.setCreatedAt(value.getCreatedAt());
                    return userEntity;
    }
}
