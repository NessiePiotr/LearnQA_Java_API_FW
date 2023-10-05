package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedTest(){
        //GENERATE USER
        Map<String, String> userData = DataGenerate.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/"+ userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/"+ userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);

    }

    @Test
    public void testEditNotAuthTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerate.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);

        int userId = this.getIntFromJson(responseCreateAuth, "id");

        // EDIT DATA
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditData = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/"+ userId, editData);

        Assertions.assertResponseCodeEquals(responseEditData, 400);
        Assertions.assertResponseTextEquals(responseEditData, "Auth token not supplied");
    }

    @Test
    public void testEditAnotherUserAuthTest() {
        //GENERATE User1 & LOGIN
        Map<String, String> userData = DataGenerate.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);
        int newUserId = this.getIntFromJson(responseCreateAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //GENERATE User2
        Map<String, String> userData2 = DataGenerate.getRegistrationData();
        Response responseCreateAuth2 = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData2);
        int newUserId2 = this.getIntFromJson(responseCreateAuth2, "id");

        // EDIT DATA: Auth User1, id User2 (!!! No error message here !!!)
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/"+newUserId2,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData);
        Assertions.assertResponseCodeEquals(responseEditData, 200);

        // CHECK DATA: Data was changed for Auth User1
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/"+newUserId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertJsonByName(responseUserData, "id", newUserId);
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditWrongEmailTest() {
        // GENERATE USER & LOGIN
        Map<String, String> userData = DataGenerate.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);
        int newUserId = this.getIntFromJson(responseCreateAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        // EDIT DATA
        String newName = "wrongemailexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newName);

        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/"+newUserId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData);

        Assertions.assertResponseCodeEquals(responseEditData, 400);
        Assertions.assertResponseTextEquals(responseEditData, "Invalid email format");
    }

    @Test
    public void testEditShortFirstNameTest() {
        // GENERATE USER & LOGIN
        Map<String, String> userData = DataGenerate.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);
        int newUserId = this.getIntFromJson(responseCreateAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        // EDIT DATA
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "1");

        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/"+newUserId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData);

        responseEditData.prettyPrint();
        Assertions.assertResponseCodeEquals(responseEditData, 400);
        Assertions.assertJsonByName(responseEditData, "error", "Too short value for field firstName");
    }
}
