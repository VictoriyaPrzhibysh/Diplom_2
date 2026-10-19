package data;

import java.util.List;

public class OrderData {

    public static final String ORDER_CREATE_PATH = "/api/orders";

    public static final List<String> VALID_INGREDIENTS = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");

    public static final List<String>  EMPTY_INGREDIENTS = List.of();

    public static final List<String> INCORRECT_INGREDIENTS = List.of("incorrect_hash_123", "incorrect_hash_124");

}