package by.baby.event;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreatedPaymentEvent {
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
}
