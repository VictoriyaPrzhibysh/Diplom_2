import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserLoginRequestModel;
import model.UserModel;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static data.UserData.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static steps.UserSteps.*;

public class UserLoginTest extends BaseApiTest {

    private UserModel user;
    private String accessToken;

    @Before
    public void createUserForTest() {
        // Создаём пользователя
        user = new UserModel(getRandomEmail(), getRandomPassword(), FIRSTNAME);
        Response createResponse = createUser(user);
        createResponse
                .then()
                .log().all()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Проверяем, что пользоватеть может авторизоваться и успешный запрос возвращает токен")
    public void testLoginUserSuccess() {

        // Логинимся со всеми обязательными полями
        UserLoginRequestModel loginRequest = new UserLoginRequestModel(user.getEmail(), user.getPassword());
        Response loginResponse = loginUser(loginRequest);
        // Получаем токен для удаления пользователя
        accessToken = loginResponse.jsonPath().getString("accessToken");
        loginResponse
                .then()
                .log().all()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("user.name", equalTo(FIRSTNAME))
                .body("user.email",equalTo(user.getEmail()));

    }
    @Test
    @DisplayName("Логин пользователя в неверным паролем")
    @Description("Проверяем, что система вернёт ошибку, если неправильно указать пароль")
    public void testLoginUserFailsBadPassword() {

        // Логинимся с несуществующем паролем
        UserLoginRequestModel loginRequestBad = new UserLoginRequestModel(user.getEmail(), "1");
        Response loginResponseBad = loginUser(loginRequestBad);
        loginResponseBad
                .then()
                .log().all()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", Matchers.containsString("email or password are incorrect"));

    }

    @Test
    @DisplayName("Логин пользователя в неверной почтой")
    @Description("Проверяем, что система вернёт ошибку, если неправильно указать почту")
    public void testLoginUserFailsBadLogin() {
        // Логинимся с несуществующей почтой
        UserLoginRequestModel loginRequestBad = new UserLoginRequestModel("n@ya.ru", user.getPassword());
        Response loginResponseBad = loginUser(loginRequestBad);
        loginResponseBad
                .then()
                .log().all()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", Matchers.containsString("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Проверяем, что система вернёт ошибку, если если поля пароль нет")
    public void testLoginUserFailsWithoutPasswordField() {
        // Логинимся не заполняя поле пароль
        UserLoginRequestModel loginRequestWithoutPassword =
                new UserLoginRequestModel(user.getEmail(), null);

        Response response = loginUser(loginRequestWithoutPassword);
        response
                .then()
                .log().all()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя без почты")
    @Description("Проверяем, что система вернёт ошибку, если если поля почта нет")
    public void testLoginUserFailsWithoutLoginField() {
        // Логинимся не заполняя поле почта
        UserLoginRequestModel loginRequestWithoutLogin =
                new UserLoginRequestModel(null, user.getPassword());

        Response response = loginUser(loginRequestWithoutLogin);
        response
                .then()
                .log().all()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString("email or password are incorrect"));
    }

    @After
    public void deleteUserAfterTest() {
        // удаляем пользователя,
        if (accessToken != null && !accessToken.isEmpty()) {
            Response deleteResponse = UserSteps.deleteUser(accessToken);
            deleteResponse.then().statusCode(SC_ACCEPTED);
        }
    }

}
