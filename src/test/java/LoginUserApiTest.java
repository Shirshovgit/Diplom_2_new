import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class LoginUserApiTest extends BaseTest {

    private String jsonBody = userFieldsCreate();

    private List<String> bodyRequestLoginUser = Arrays.asList("{\n" +
            "\"email\": \"test-data@yandex.ru\",\n" +
            "\"name\": \"Username\"\n" +
            "}", "{\n" +
            "\"password\": \"1234\",\n" +
            "\"name\": \"Username\"\n" +
            "}", "{\n" +
            "\"password\": \"1234\",\n" +
            "\"email\": \"test-data\"\n" +
            "}", "{\n" +
            "\"password\": \"\",\n" +
            "\"email\": \"test-data@yandex.ru\"\n" +
            "}");

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Провреяем, что можно успешно авторизоваться под существующим пользователем")
    public void shouldSuccessLoginUser() {

        Response createUser = sendPostRequest(pathCreateUser, jsonBody);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response loginUser = sendPostRequest(pathLoginUser, jsonBody);
        compareStatusCodeResponse(loginUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(loginUser, "success", true);

    }

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Провреяем, что при неверном логине/пароле или отсутсвии одного из полей авторизация не произойдет")
    public void shouldFailLoginUser() {
        for (String body : bodyRequestLoginUser) {
            Response loginUser = sendPostRequest(pathLoginUser, body);
            compareStatusCodeResponse(loginUser, statusCode.FAILED_401.code);
            compareBodyResponse(loginUser, "success", false);
            compareBodyResponse(loginUser, "message", "email or password are incorrect");
        }
    }

    @After
    @Step("Удаляем юзера по завершению теста")
    public void deleteUserAfterTest() {
        Response checkLogin = sendPostRequest(pathLoginUser, jsonBody);
        if (getAccessToken(checkLogin) != null) {
            Response deleteUser = sendDeleteRequest(pathAuthUser, getAccessToken(checkLogin));
            compareBodyResponse(deleteUser, "success", true);
            compareBodyResponse(deleteUser, "message", "User successfully removed");
            compareStatusCodeResponse(deleteUser, BaseTest.statusCode.SUCCESS_202.code);
        }
    }
}
