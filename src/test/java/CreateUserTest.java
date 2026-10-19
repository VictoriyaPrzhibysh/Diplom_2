import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserLoginRequestModel;
import model.UserModel;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import steps.UserSteps;

import static data.UserData.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static steps.UserSteps.*;

public class CreateUserTest extends BaseApiTest {

    private UserModel user;
    private String accessToken;

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяем, что уникального пользователя можно создать")
    public void testCreateUserSuccess() {
        // Создаём пользователя
        user = new UserModel(getRandomEmail(), getRandomPassword(), FIRSTNAME);
        Response response = createUser(user);
        response
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getFirstName()))
                .body("user.email", equalTo(user.getEmail()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("accessToken", not(equalTo("")))
                .body("refreshToken", not(equalTo("")));
        // Сохраняем токен для удаления пользователя
        accessToken = response.jsonPath().getString("user.accessToken");
    }

    @Test
    @DisplayName("Создание пользователя который уже существует")
    @Description("Проверяем, что нельзя создать двух одинаковых пользователей")
    public void testCreateDuplicateUserFails() {
        // Создаём первого пользователя
        user = new UserModel(getRandomEmail(), getRandomPassword(), FIRSTNAME);
        Response response = createUser(user);
        response
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getFirstName()))
                .body("user.email", equalTo(user.getEmail()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("accessToken", not(equalTo("")))
                .body("refreshToken", not(equalTo("")));
        // Сохраняем токен для удаления пользователя
        accessToken = response.jsonPath().getString("user.accessToken");

        // Создаем второго пользователя с теми же данными
        createUser(user)
                .then()
                .log().all()
                .statusCode(403)
                .body("message", Matchers.containsString("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без почты")
    @Description("Проверяем, что чтобы создать пользователя, нужно передать в ручку все обязательные поля(без почты)")
    public void testCreateUserWithoutEmailFails() {
        // Создаём пользователя без почты
        user = new UserModel(null, getRandomPassword(), FIRSTNAME);

        createUser(user)
                .then()
                .log().all()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверяем, что чтобы создать пользователя, нужно передать в ручку все обязательные поля(без пароля)")
    public void testCreateUserWithoutPasswordFails() {
        // Создаём пользователя без пароля
        user = new UserModel(getRandomEmail(), null, FIRSTNAME);
        createUser(user)
                .then()
                .log().all()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создаем пользователя без имени")
    @Description("Проверяем, что чтобы создать пользователя, нужно передать в ручку все обязательные поля(без имени)")
    public void testCreateUserWithoutFirstNameFails() {
        // Создаём пользователя без имени
        user = new UserModel(getRandomEmail(), getRandomPassword(), null);
        createUser(user)
                .then()
                .log().all()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void deleteUserAfterTest() {
        // удаляем пользователя,
        if (accessToken != null && !accessToken.isEmpty()) {
            Response deleteResponse = UserSteps.deleteUser(accessToken);
            deleteResponse.then().statusCode(200);
        }
    }
}
