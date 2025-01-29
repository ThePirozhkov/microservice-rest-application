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

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    private final UserEntity user1 = new UserEntity();
    private final UserEntity user2 = new UserEntity();
    private final UserEntity user3 = new UserEntity();

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        user1.setUsername("user1");
        user1.setAuthToken("authtoken1");
        user1.setMoney(0L);
        user1.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user1);
        user2.setUsername("user2");
        user2.setAuthToken("authtoken2");
        user2.setMoney(0L);
        user2.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user2);
        user3.setUsername("user3");
        user3.setAuthToken("authtoken3");
        user3.setMoney(0L);
        user3.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user3);
    }

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    @Transactional(readOnly = true)
    public void shouldFindAllSuccessfully() {
        assertThat(userService.findAll()).hasSize(3);
    }

    @Test
    @Order(2)
    @Transactional(readOnly = true)
    public void shouldFindByIdSuccessfully() {
        assertThat(userService.findById(4L)).isNotEmpty();
    }

    @Test
    @Order(3)
    public void shouldSaveUserSuccessfully() {
        UserDto userDto = userService.save(new UserDto(
                "username", "authToken", 0L
        ))
                .get();
        assertThat(userService.findById(userDto.getId())).isNotEmpty();
    }

    @Test
    @Order(4)
    public void shouldUpdateUserSuccessfully() {
        UserDto userDto = new UserDto(
                "username228", "authToken228", 100L
        );
        assertThat(userService.update(userDto, user1.getId()).get())
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(userDto);
    }

    @Test
    @Order(5)
    public void shouldDeleteUserSuccessfully() {
        if (userRepository.existsById(user1.getId())) {
            userRepository.deleteById(user1.getId());
            assertThat(userRepository.findById(user1.getId())).isEmpty();
        } else {
            throw new RuntimeException();
        }
    }
}
