package by.baby.usermicroservice.persistence.repository;

import by.baby.usermicroservice.persistence.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldCreateSaveAndFindUserSuccessfully() {
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
