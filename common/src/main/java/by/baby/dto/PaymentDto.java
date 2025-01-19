package by.baby.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class PaymentDto {

    private Long id;
    private UserDto fromUser;
    private UserDto toUser;
    private BigDecimal amount;
    private Date paymentDate;

    @JsonCreator
    public PaymentDto(UserDto fromUser, UserDto toUser, BigDecimal amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }
}
