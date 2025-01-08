package by.baby.usermicroservice.persistence.repository;

import by.baby.usermicroservice.UserMicroserviceApplication;
import by.baby.usermicroservice.persistence.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest(classes = UserMicroserviceApplication.class)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateSaveAndFindUserSuccessfully() {
        UserEntity testUser = new UserEntity();
        testUser.setUsername("testUsername1");
        testUser.setAuthToken(UUID.randomUUID().toString());
        testUser.setMoney(0L);
        userRepository.saveAndFlush(testUser);
        UserEntity retrievedUser = userRepository.findById(testUser.getId())
                .orElseThrow(RuntimeException::new);
        Assertions.assertEquals(testUser, retrievedUser);
    }

}
