package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class UserModel {
    private String email;
    private String password;
    private String name;
}
