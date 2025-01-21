package by.baby.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CreatedPaymentDto {
    private String fromUserId;
    private String toUserId;
    private BigDecimal amount;
}
