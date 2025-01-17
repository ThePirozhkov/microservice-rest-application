package by.baby.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity fromUser;

    @ManyToOne
    private UserEntity toUser;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Date paymentDate;

}
