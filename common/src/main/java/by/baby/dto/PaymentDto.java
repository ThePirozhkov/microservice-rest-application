package by.baby.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
final public class PaymentDto implements Dto {

    private String id;
    private UserDto fromUser;
    private UserDto toUser;
    private BigDecimal amount;
    private Instant paymentDate;

}
