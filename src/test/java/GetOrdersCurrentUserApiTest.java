import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import POJO.Ingredients;
import org.junit.After;
import org.junit.Test;

public class GetOrdersCurrentUserApiTest extends BaseTest {

    @Test
    @DisplayName("Получение заказов конкретного пользователя:")
    @Description("Провреяем получение списка заказов, если передан токен авторизации")
    public void shouldSuccessGetOrdersListRequestWithAccessToken() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);
        String accessToken = getAccessToken(createUser);

        Response ingredientsGet = sendGetRequest(pathIngredients);

        Ingredients ingredients = new Ingredients(getIngredients(ingredientsGet));

        Response createOrder = sendPostRequest(pathOrder, ingredients, getAccessToken(createUser));
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createOrder, "success", true);

        Response getOrders = sendGetRequest(pathOrder, accessToken);
        compareStatusCodeResponse(getOrders, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(getOrders, "success", true);

    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя:")
    @Description("Провреяем получение списка заказов, если непередан токен авторизации")
    public void shouldSuccessGetOrdersListRequestWithOutAccessToken() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response ingredientsGet = sendGetRequest(pathIngredients);

        Ingredients ingredients = new Ingredients(getIngredients(ingredientsGet));

        Response createOrder = sendPostRequest(pathOrder, ingredients);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createOrder, "success", true);

        Response getOrders = sendGetRequest(pathOrder);
        compareStatusCodeResponse(getOrders, statusCode.FAILED_401.code);
        compareBodyResponse(getOrders, "success", false);
        compareBodyResponse(getOrders, "message", "You should be authorised");

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
