package by.baby.paymentmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "by.baby.persistence.repository"
)
@EntityScan(
        basePackages = "by.baby.persistence.entity"
)
public class PaymentMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMicroserviceApplication.class, args);
    }

}
