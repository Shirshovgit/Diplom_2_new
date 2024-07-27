import POJO.UserData;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CreateUserApiTest extends BaseTest {

    private final List<UserData> bodyRequestCreateUser = Arrays.asList(userWithoutPassword, userWithoutEmail, userWithoutName);

    @Test
    @DisplayName("Создание пользователя")
    @Description("Провреяем, что можно успешно создать нового пользователя")
    public void shouldSuccessCreateUser() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегестрирован")
    @Description("Провреяем, повторное создание пользователя и возврат ошибки на попытку создать такого пользователя")
    public void shouldFailWithRepeatedCreateUser() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response createRepeatedUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createRepeatedUser, statusCode.FAILED_403.code);
        compareBodyResponse(createRepeatedUser, "success", false);
        compareBodyResponse(createRepeatedUser, "message", "User already exists");

    }

    @Test
    @DisplayName("Создание пользователя, не заполнено одно из полей")
    @Description("Провреяем, создание пользователя, если не передать одно из полей email/password/userName")
    public void shouldFailCreateUser() {
        for (UserData body : bodyRequestCreateUser) {

            Response createUser = sendPostRequest(pathCreateUser, body);
            compareStatusCodeResponse(createUser, statusCode.FAILED_403.code);
            compareBodyResponse(createUser, "success", false);
            compareBodyResponse(createUser, "message", "Email, password and name are required fields");
        }
    }

    @After
    @Step("Удаляем юзера по завершению теста")
    public void deleteUserAfterTest() {
        Response checkLogin = sendPostRequest(pathLoginUser, userFullData);
        if (getAccessToken(checkLogin) != null) {
            Response deleteUser = sendDeleteRequest(pathAuthUser, getAccessToken(checkLogin));
            compareBodyResponse(deleteUser, "success", true);
            compareBodyResponse(deleteUser, "message", "User successfully removed");
            compareStatusCodeResponse(deleteUser, BaseTest.statusCode.SUCCESS_202.code);
        }
    }

}
