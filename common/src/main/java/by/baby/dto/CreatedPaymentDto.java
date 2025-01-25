package by.baby.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreatedPaymentDto {

    @NotNull
    @NotBlank
    private String fromUserId;
    @NotNull
    @NotBlank
    private String toUserId;
    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

}
