package by.baby.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
final public class CreatedPaymentDto implements Dto {

    @NotNull
    @Positive
    private Long fromUserId;
    @NotNull
    @Positive
    private Long toUserId;
    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

}
