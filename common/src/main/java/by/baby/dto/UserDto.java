package by.baby.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public final class UserDto implements Dto {

    public UserDto(String username, String authToken, Long money) {
        this.money = money;
        this.authToken = authToken;
        this.username = username;
    }

    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String authToken;
    @NotNull
    @PositiveOrZero
    private Long money;
    private Instant createdAt;

}
