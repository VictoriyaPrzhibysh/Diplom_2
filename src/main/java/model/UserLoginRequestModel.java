package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class UserLoginRequestModel {
    private String login;
    private String password;
}
