package by.baby.usermicroservice.service;

import by.baby.usermicroservice.dto.UserDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    void shouldFindAllSuccessfully() {
        assertThat(userService.findAll()).isNotEmpty().hasSize(3);
    }

    @Test
    @Order(2)
    void shouldFindByIdSuccessfully() {
        assertThat(userService.findById(1L)).isNotEmpty();
    }

    @Test
    @Order(3)
    void shouldSaveSuccessfully() {
        UserDto userDto = userService.save(new UserDto(
                "username", "authToken", 0L, new Date()
        ));
        assertThat(userService.findById(userDto.getId())).isNotEmpty();
    }
}
