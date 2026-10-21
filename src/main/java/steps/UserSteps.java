package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import model.UserLoginRequestModel;
import model.UserModel;

import static data.UserData.*;
import static io.restassured.RestAssured.given;

public class UserSteps {
    @Step("Создание пользователя")
    public static Response createUser(UserModel user) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(USER_CREATE_PATH)
                .then()
                .extract().response();
    }
    @Step("Авторизация пользователя")
    public static Response loginUser(UserLoginRequestModel request) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(USER_AUTHORIZATION_PATH)
                .then()
                .extract().response();
    }
    @Step("Удаление пользователя по токену")
    public static Response deleteUser(String accessToken) {

        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete(USER_DELETE_PATH)
                .then()
                .extract().response();
    }
}
