package by.baby.dto;

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
    private String username;
    private String authToken;
    private Long money;
    private Date createdAt;
}
