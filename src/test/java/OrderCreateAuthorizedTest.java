import data.OrderData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.OrderModel;
import model.UserLoginRequestModel;
import model.UserModel;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;

import static data.UserData.*;
import static org.hamcrest.Matchers.*;
import static steps.UserSteps.*;

public class OrderCreateAuthorizedTest extends BaseApiTest {

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
                .statusCode(200)
                .body("success", equalTo(true));
        // Получаем токен для удаления
        accessToken = createResponse.jsonPath().getString("accessToken");
        // Логинимся
        UserLoginRequestModel loginRequest = new UserLoginRequestModel(user.getEmail(), user.getPassword());
        Response loginResponse = loginUser(loginRequest);
        loginResponse
                .then()
                .log().all()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("accessToken", not(equalTo("")));

    }

    @Test
    @DisplayName("Создание заказа авторизированным пользователем с ингредиентами")
    @Description("Проверяем, что авторизированный пользователь может создать заказ с ингрединетами")
    public void testCreateOrderAuthorizedUser () {

        OrderModel order = new OrderModel(OrderData.VALID_INGREDIENTS);
        Response response = OrderSteps.createOrder(order, accessToken);
        response.then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.number", not(equalTo("")))
                .body("order.owner.name", equalTo(user.getFirstName()))
                .body("order.owner.email", equalTo(user.getEmail()));
    }


    @Test
    @DisplayName("Создание заказа авторизированным пользователем без ингредиентов")
    @Description("Проверяем, что авторизированный пользователь не может создать заказ без ингредиентов")

    public void testCreateOrderEmptyIngredientsAuthorizedUser () {

        OrderModel order = new OrderModel(OrderData.EMPTY_INGREDIENTS);
        Response response = OrderSteps.createOrder(order, accessToken);
        response.then()
                .log().all()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", Matchers.containsString("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа авторизированным пользователем с неверным хешем ингредиентов")
    @Description("Проверяем, что авторизированный пользователь не может создать заказ с неверным хешем ингрединетов")

        public void testCreateOrderIncorrectIngredientsAuthorizedUser () {

            OrderModel order = new OrderModel(OrderData.INCORRECT_INGREDIENTS);
            Response response = OrderSteps.createOrder(order, accessToken);
            response.then()
                    .log().all()
                    .statusCode(400);
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
