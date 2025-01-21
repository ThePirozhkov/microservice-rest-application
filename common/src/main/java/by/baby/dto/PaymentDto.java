package by.baby.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PaymentDto {

    private Long id;
    private UserDto fromUser;
    private UserDto toUser;
    private BigDecimal amount;
    private Date paymentDate;

}
