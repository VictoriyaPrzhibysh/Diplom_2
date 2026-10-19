package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.OrderModel;

import static data.OrderData.ORDER_CREATE_PATH;
import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Создание заказа")
    public static Response createOrder(OrderModel order, String accessToken) {
        RequestSpecification spec = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(order);
        if (accessToken != null && !accessToken.isEmpty()) {
            spec.header("Authorization", "Bearer " + accessToken);
        }

        return spec.when()
                .post(ORDER_CREATE_PATH)
                .then()
                .log().all()
                .extract().response();
    }

}

