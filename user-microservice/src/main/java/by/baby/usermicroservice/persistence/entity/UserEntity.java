package by.baby.usermicroservice.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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
