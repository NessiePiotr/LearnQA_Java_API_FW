package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import lib.ApiCoreRequests;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


public class UserRegisterTest  extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerate.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '"+ email +"' already exists");
    }

    @Test
    public void testCreateUserSuccessfully(){

        Map<String, String> userData = DataGenerate.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    public void testCreateUserWithIncorrectEmail(){
        String email = "vinkotovexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerate.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1,   10,  The value of 'firstName' field is too short",
            "10,  1,   The value of 'lastName' field is too short",
            "251, 10,  The value of 'firstName' field is too long",
            "10,  251, The value of 'lastName' field is too long"})
    public void testCreateUserWithDifferentName(int firstNameSize, int lastNameSize, String expectedMessage){
        Map<String, String> userData = new HashMap<>();

        userData.put("firstName", DataGenerate.getRandomStringWithGivenSize(firstNameSize));
        userData.put("lastName", DataGenerate.getRandomStringWithGivenSize(lastNameSize));
        userData = DataGenerate.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, expectedMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutOneField(String notExpectedFiledName){
        Map<String, String> userData =
                DataGenerate.getRegistrationDataWithoutOneField(notExpectedFiledName);

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "The following required params are missed: " + notExpectedFiledName);
    }
}
