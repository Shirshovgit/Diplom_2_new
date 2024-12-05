import POJO.UserData;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class UpdateUserFieldsApiTest extends BaseTest {

    private final Faker faker = new Faker();

    @Test
    @DisplayName("Обновление данных пользователя")
    @Description("Провреяем, что можно успешно обновить даннные пользователя")
    public void shouldSuccessUpdateUser() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);
        String userData = createUser.jsonPath().get("user").toString();

        UserData userFullDataUpdate = new UserData(faker.name().fullName(), faker.name().firstName() + "2@yandex.ru", "password");
        Response updateUser = sendPathRequest(pathAuthUser, userFullDataUpdate, getAccessToken(createUser));
        compareStatusCodeResponse(updateUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(updateUser, "success", true);
        String userUpdateData = updateUser.jsonPath().get("user").toString();

        Assert.assertNotEquals(userData, userUpdateData);
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    @Description("Провреяем, что для не авторизованного пользователя обновление данных не произойдет")
    public void shouldFailUpdateUser() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        UserData userFullDataUpdate = new UserData(faker.name().fullName(), faker.name().firstName() + "@yandex.ru", "password");
        Response updateUser = sendPathRequest(pathAuthUser, userFullDataUpdate);
        compareStatusCodeResponse(updateUser, statusCode.FAILED_401.code);
        compareBodyResponse(updateUser, "success", false);
        compareBodyResponse(updateUser, "message", "You should be authorised");

    }

    @Test
    @DisplayName("Обновление данных пользователя")
    @Description("Провреяем, что вернется ошибка, если при изменении данных указана уже ранее используемая почта")
    public void shouldFailUpdateUserEmailAlreadyExists() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);
        String jsonBodyUpdateUser = "{\n" +
                "\"email\": \"test-data@yandex.ru\",\n" +
                "\"name\": \"Username\"\n" +
                "}";
        Response updateUser = sendPathRequest(pathAuthUser, jsonBodyUpdateUser, getAccessToken(createUser));
        compareStatusCodeResponse(updateUser, statusCode.FAILED_403.code);
        compareBodyResponse(updateUser, "success", false);
        compareBodyResponse(updateUser, "message", "User with such email already exists");
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
