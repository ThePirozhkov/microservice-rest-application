package by.baby.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PaymentEntity> payments = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        this.createdAt = Instant.now();
    }
}
