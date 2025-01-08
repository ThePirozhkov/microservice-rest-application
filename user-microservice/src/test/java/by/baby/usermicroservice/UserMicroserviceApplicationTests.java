package by.baby.usermicroservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = UserMicroserviceApplication.class)
@ActiveProfiles("test")
class UserMicroserviceApplicationTests {

    @Test
    void contextLoads() {
    }

}
