package by.baby.usermicroservice.controller.rest;

import by.baby.entity.UserEntity;
import by.baby.dto.UserDto;
import by.baby.spring.components.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/user_microservice_test_cleanup_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class UserRestControllerTest {

    private final UserEntity user1 = new UserEntity();
    private final UserEntity user2 = new UserEntity();

    @BeforeEach
    public void setup() {
        this.user1.setUsername("user1");
        this.user1.setAuthToken("authtoken1");
        this.user1.setMoney(0L);
        this.user1.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user1);
        this.user2.setUsername("user2");
        this.user2.setAuthToken("authtoken2");
        this.user2.setMoney(0L);
        this.user2.setCreatedAt(Instant.now());
        userRepository.saveAndFlush(user2);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @SneakyThrows
    @Test
    public void shouldGetAllUsersSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.length()").value(2));
        System.out.println(userRepository.findAll());
    }

    @SneakyThrows
    @Test
    public void shouldGetUserSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.id").value(user1.getId()));
    }

    @SneakyThrows
    @Test
    public void shouldGetNotFoundUserException() {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void shouldCreateUserSuccessfully() {
        UserDto userDto = new UserDto(
                "createUserUsername", "authTokenCreateUserTest", 0L
        );
        String jsonUserDto = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDto))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.username").value(userDto.getUsername()));
        assertThat(userRepository.findByUsername(userDto.getUsername())).isNotEmpty();
    }

    @SneakyThrows
    @Test
    public void shouldUpdateUserSuccessfully() {
        UserDto updUser = new UserDto(
                "updateUserUsername", "authTokenUpdateUserTest", 0L
        );
        String jsonUpdUserDto = objectMapper.writeValueAsString(updUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/user/" + user1.getId())
                        .content(jsonUpdUserDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.id").value(user1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.username").value(updUser.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.authToken").value(updUser.getAuthToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returned.money").value(updUser.getMoney()));
    }

    @SneakyThrows
    @Test
    public void shouldGetUserNotFoundExceptionForUpdate() {
        UserDto updUser = new UserDto(
                "updateUserUsername", "authTokenUpdateUserTest", 0L
        );
        String jsonUpdUserDto = objectMapper.writeValueAsString(updUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/user/3")
                .content(jsonUpdUserDto)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void shouldDeleteUserSuccessfully() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + user1.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        assertThat(userRepository.findById(user1.getId())).isEmpty();
    }

    @SneakyThrows
    @Test
    public void shouldGetUserNotFoundExceptionForDelete() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
