package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class UserLoginRequestModel {
    private String email;
    private String password;
}
