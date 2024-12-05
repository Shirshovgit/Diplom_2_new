import POJO.UserData;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class LoginUserApiTest extends BaseTest {

    private final List<UserData> bodyRequestLoginUser = Arrays.asList(userWithoutPassword, userWithoutEmail, userWithoutName, userPasswordIsNull);

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Провреяем, что можно успешно авторизоваться под существующим пользователем")
    public void shouldSuccessLoginUser() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response loginUser = sendPostRequest(pathLoginUser, userFullData);
        compareStatusCodeResponse(loginUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(loginUser, "success", true);

    }

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Провреяем, что при неверном логине/пароле или отсутсвии одного из полей авторизация не произойдет")
    public void shouldFailLoginUser() {
        for (UserData body : bodyRequestLoginUser) {
            Response loginUser = sendPostRequest(pathLoginUser, body);
            compareStatusCodeResponse(loginUser, statusCode.FAILED_401.code);
            compareBodyResponse(loginUser, "success", false);
            compareBodyResponse(loginUser, "message", "email or password are incorrect");
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
