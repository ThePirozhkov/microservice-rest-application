package by.baby.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public final class UserDto {

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
    private Date createdAt;

}
