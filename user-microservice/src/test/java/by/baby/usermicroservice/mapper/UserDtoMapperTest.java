package by.baby.usermicroservice.mapper;

import by.baby.entity.UserEntity;
import by.baby.spring.components.mapper.UserDtoMapper;
import by.baby.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class UserDtoMapperTest {

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Test
    public void shouldMapEntityToDtoSuccessfully() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("username");
        userEntity.setAuthToken("authToken");
        userEntity.setMoney(0L);
        userEntity.setCreatedAt(new Date());
        assertThat(userDtoMapper.mapToDto(userEntity)).isNotNull();
    }

    @Test
    public void shouldMapDtoToEntitySuccessfully() {
        UserDto userDto = new UserDto(
                1L, "username", "authToken", 0L, new Date()
        );
        assertThat(userDtoMapper.mapToEntity(userDto)).isNotNull();
    }

    @Test
    public void shouldMapEntityToDtoWithNullAndGetRuntimeExceptionSuccessfully() {
        assertThatThrownBy(() -> userDtoMapper.mapToDto(null)).isInstanceOf(RuntimeException.class);
    }

}
