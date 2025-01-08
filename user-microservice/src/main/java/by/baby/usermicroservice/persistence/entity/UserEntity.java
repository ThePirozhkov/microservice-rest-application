package by.baby.usermicroservice.persistence.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String authToken;

    @Column(nullable = false)
    @Check(name = "money_ck", constraints = "money >= 0")
    private Long money;

    @Column(nullable = false)
    private Date createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = new Date();
    }
}
