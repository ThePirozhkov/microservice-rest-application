package by.baby.event;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreatedPaymentEvent {
    private String fromUserId;
    private String toUserId;
    private BigDecimal amount;
}
