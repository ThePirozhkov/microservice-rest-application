package by.baby.usermicroservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public final class UserDto {

    private Long id;
    private String username;
    private String authToken;
    private Long money;
    private Date createdAt;

    @JsonCreator
    public UserDto(String username, String authToken, Long money) {
        this.username = username;
        this.authToken = authToken;
        this.money = money;
    }
}
