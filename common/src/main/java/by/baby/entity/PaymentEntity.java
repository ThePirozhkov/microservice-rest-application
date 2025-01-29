package by.baby.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PaymentEntity {

    @Id
    private String id;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity fromUser;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity toUser;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant paymentDate;

    @PrePersist
    private void onCreate() {
        this.paymentDate = Instant.now();
    }

}
