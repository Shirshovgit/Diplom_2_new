import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import POJO.Ingredients;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class CreateOrderApiTest extends BaseTest {

    private String ingredientsNull = "{\n" +
            "\"ingredients\": []\n" +
            "}";

    private String ingredientsInvalidHash = "{\n" +
            "\"ingredients\": [\"61c0c5a71d1f82001bdaa0\"]\n" +
            "}";


    @Test
    @DisplayName("Cоздание заказа")
    @Description("Провреяем, что можно успешно создать заказ")
    public void shouldSuccessCreateOrder() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response ingredientsGet = sendGetRequest(pathIngredients);

        Ingredients ingredients = new Ingredients(getIngredients(ingredientsGet));

        Response createOrder = sendPostRequest(pathOrder, ingredients, getAccessToken(createUser));
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createOrder, "success", true);

    }

    @Test
    @DisplayName("Cоздание заказа")
    @Description("Провреяем, cоздание заказа, если не была передана авторизацию юзера")
    public void shouldSuccessCreateOrderWithOutAuthorization() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response ingredientsGet = sendGetRequest(pathIngredients);

        Ingredients ingredients = new Ingredients(getIngredients(ingredientsGet));

        Response createOrder = sendPostRequest(pathOrder, ingredients);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createOrder, "success", true);

    }

    @Test
    @DisplayName("Cоздание заказа")
    @Description("Провреяем, cоздание заказа, если не была передана авторизацию юзера")
    public void shouldFailCreateOrderWithOutIngredients() {

        Response createUser = sendPostRequest(pathCreateUser, userFullData);
        compareStatusCodeResponse(createUser, BaseTest.statusCode.SUCCESS_200.code);
        compareBodyResponse(createUser, "success", true);

        Response createOrder = sendPostRequest(pathOrder, ingredientsNull);
        compareStatusCodeResponse(createOrder, statusCode.FAILED_400.code);
        compareBodyResponse(createOrder, "success", false);
        compareBodyResponse(createOrder, "message", "Ingredient ids must be provided");

    }

    @Test
    @DisplayName("Cоздание заказа")
    @Description("Провреяем, cоздание заказа, если был передан не валидный хэш ингридиента")
    public void shouldFailCreateOrderWithIngredientsInvalidHash() {

        Response createOrder = sendPostRequest(pathOrder, ingredientsInvalidHash);
        Assert.assertEquals(statusCode.FAILED_500.code, createOrder.getStatusCode());
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
