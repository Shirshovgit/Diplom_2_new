import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;

import java.util.Random;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class BaseTest {

    public String pathCreateUser = "/api/auth/register";
    public String pathLoginUser = "/api/auth/login";

    public String pathAuthUser = "/api/auth/user";

    public String pathOrder = "/api/orders";

    enum statusCode {
        SUCCESS_200(200),

        SUCCESS_202(202),
        FAILED_401(401),
        FAILED_400(400),
        FAILED_403(403),
        FAILED_500(500);


        final int code;

        statusCode(int code) {
            this.code = code;
        }

    }

    public String ingredients = "{\n" +
            "\"ingredients\": [\"61c0c5a71d1f82001bdaaa70\",\"61c0c5a71d1f82001bdaaa75\"]\n" +
            "}";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }


    public Response sendPostRequest(String pathRequest, String pathBody) {
        step("Отправляем Post в ручку " + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .body(pathBody)
                    .post(pathRequest);
            return response;
        }
    }

    public Response sendPostRequest(String pathRequest, String pathBody, String bearerToken) {
        step("Отправляем Post в ручку " + pathRequest);
        {
            Response response = given()
                    .header("Content-type", "application/json")
                    .header("authorization", bearerToken)
                    .body(pathBody)
                    .post(pathRequest);
            return response;
        }
    }

    public Response sendPathRequest(String pathRequest, String pathBody, String bearerToken) {
        step("Отправляем Post в ручку " + pathRequest);
        {
            Response response = given()
                    .header("Content-type", "application/json")
                    .header("authorization", bearerToken)
                    .body(pathBody)
                    .patch(pathRequest);
            return response;
        }
    }

    public Response sendPathRequest(String pathRequest, String pathBody) {
        step("Отправляем Post в ручку " + pathRequest);
        {
            Response response = given()
                    .header("Content-type", "application/json")
                    .body(pathBody)
                    .patch(pathRequest);
            return response;
        }
    }

    public Response sendGetRequest(String pathRequest) {
        step("Отправляем Get в ручку" + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .get(pathRequest);
            return response;
        }
    }

    public Response sendGetRequest(String pathRequest, String bearerToken) {
        step("Отправляем Get в ручку" + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .header("authorization", bearerToken)
                    .get(pathRequest);
            return response;
        }
    }

    public Response sendDeleteRequest(String pathRequest, String bearerToken) {
        step("Отправляем Delete в ручку" + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .header("authorization", bearerToken)
                    .delete(pathRequest);
            return response;
        }
    }

    public String userFieldsCreate() {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        String userName = "name" + random.nextInt(10000000);
        String json = "{\"email\": \"" + email + "\", \"password\": \"aaa\", \"name\": \"" + userName + "\"}";
        return json;
    }

    @Step("Сравниваем статус кода ответа")
    public void compareStatusCodeResponse(Response response, Integer code) {
        Assert.assertTrue("Проверка завершилось ошибкой " + response.jsonPath().get().toString() + "Code: " + response.thenReturn().statusCode(),
                response.thenReturn().statusCode() == code);
    }

    @Step("Сравниваем тело ответа")
    public void compareBodyResponse(Response response, String parameterName, String messageText) {
        response.then().assertThat().body(parameterName, equalTo(messageText));
    }

    @Step("Сравниваем тело ответа")
    public void compareBodyResponse(Response response, String parameterName, boolean value) {
        response.then().assertThat().body(parameterName, equalTo(value));
    }

    @Step("Получаем accessToken юзера")
    public String getAccessToken(Response response) {
        if (response.jsonPath().get("success").toString() == "true") {
            return response.jsonPath().get("accessToken").toString();
        }
        return null;
    }
}
