package model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class OrderModel {
    private List<String> ingredients;
}
