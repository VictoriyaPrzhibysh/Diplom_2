package data;

import com.github.javafaker.Faker;

public class UserData {
    private static Faker user = new Faker();
    public static String getRandomEmail() {
        return user.internet().emailAddress();
    }
    public static String getRandomPassword() {
        return user.regexify("[0-9]{6}");
}
    public static final String FIRSTNAME = "vika";
    public static final String USER_CREATE_PATH = "/api/auth/register";
    public static final String USER_AUTHORIZATION_PATH = "/api/auth/login";
    public static final String USER_DELETE_PATH = "/api/auth/user";
}
