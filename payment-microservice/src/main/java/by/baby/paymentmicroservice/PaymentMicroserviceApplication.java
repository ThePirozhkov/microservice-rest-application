package by.baby.paymentmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = "by.baby"
)
@EntityScan(
        basePackages = "by.baby.entity"
)
@EnableJpaRepositories(
        "by.baby.spring.components.repository"
)
public class PaymentMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMicroserviceApplication.class, args);
    }

}
