import data.OrderData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.OrderModel;
import org.junit.Test;
import steps.OrderSteps;


import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;

public class OrderCreateUnauthorizedTest extends BaseApiTest {

    @Test
    @DisplayName("Создание заказа неавторизированным пользователем с ингредиентами")
    @Description("Проверяем, создание заказа неавторизированным пользователем с ингредиентами")

    public void testCreateOrderUnauthorizedUser() {
        OrderModel order = new OrderModel(OrderData.VALID_INGREDIENTS);

        Response response = OrderSteps.createOrder(order, null);

        response.then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.number", not(equalTo("")));
    }
}
