package by.baby.usermicroservice.service;

import by.baby.entity.UserEntity;
import by.baby.dto.UserDto;
import by.baby.spring.components.repository.UserRepository;
import by.baby.spring.components.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        UserEntity user1 = new UserEntity();
        user1.setUsername("user1");
        user1.setAuthToken("authtoken1");
        user1.setMoney(0L);
        user1.setCreatedAt(new Date());
        userRepository.saveAndFlush(user1);
        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");
        user2.setAuthToken("authtoken2");
        user2.setMoney(0L);
        user2.setCreatedAt(new Date());
        userRepository.saveAndFlush(user2);
        UserEntity user3 = new UserEntity();
        user3.setUsername("user3");
        user3.setAuthToken("authtoken3");
        user3.setMoney(0L);
        user3.setCreatedAt(new Date());
        userRepository.saveAndFlush(user3);
    }

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    @Transactional(readOnly = true)
    public void shouldFindAllSuccessfully() {
        assertThat(userService.findAll()).isNotEmpty().hasSize(3);
    }

    @Test
    @Order(2)
    @Transactional(readOnly = true)
    public void shouldFindByIdSuccessfully() {
        assertThat(userService.findById(4L)).isNotEmpty();
    }

    @Test
    @Order(3)
    public void shouldSaveSuccessfully() {
        UserDto userDto = userService.save(new UserDto(
                "username", "authToken", 0L
        ));
        assertThat(userService.findById(userDto.getId())).isNotEmpty();
    }
}
