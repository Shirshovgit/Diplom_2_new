import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CreateUserApiTest extends BaseTest {
    private List<String> bodyRequestCreateUser = Arrays.asList("{\n" +
            "\"email\": \"test-data@yandex.ru\",\n" +
            "\"name\": \"Username\"\n" +
            "}", "{\n" +
            "\"password\": \"1234\",\n" +
            "\"name\": \"Username\"\n" +
            "}", "{\n" +
            "\"password\": \"1234\",\n" +
            "\"email\": \"test-data@yandex.ru\"\n" +
            "}");

    private String jsonBody = userFieldsCreate();

    @Test
    @DisplayName("Создание пользователя")
    @Description("Провреяем, что можно успешно создать нового пользователя")
    public void shouldSuccessCreateUser() {

        Response createUser = sendPostRequest(pathCreateUser, jsonBody);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);
        //Проверить тело ответа на регистрацию?
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегестрирован")
    @Description("Провреяем, повторное создание пользователя и возврат ошибки на попытку создать такого пользователя")
    public void shouldFailWithRepeatedCreateUser() {

        Response createUser = sendPostRequest(pathCreateUser, jsonBody);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response createRepeatedUser = sendPostRequest(pathCreateUser, jsonBody);
        compareStatusCodeResponse(createRepeatedUser, statusCode.FAILED_403.code);
        compareBodyResponse(createRepeatedUser, "success", false);
        compareBodyResponse(createRepeatedUser, "message", "User already exists");

    }

    @Test
    @DisplayName("Создание пользователя, не заполнено одно из полей")
    @Description("Провреяем, создание пользователя, если не передать одно из полей email/password/userName")
    public void shouldFailCreateUser() {
        for (String body : bodyRequestCreateUser) {

            Response createUser = sendPostRequest(pathCreateUser, body);
            compareStatusCodeResponse(createUser, statusCode.FAILED_403.code);
            compareBodyResponse(createUser, "success", false);
            compareBodyResponse(createUser, "message", "Email, password and name are required fields");

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
