package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDeleteTest extends BaseTestCase {
    String cookie;
    String header;
    int userInOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void notAvailableDeleteTest() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.userInOnAuth = this.getIntFromJson(responseGetAuth, "user_id");

        Response responseDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userInOnAuth,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"));

        assertEquals("Please, do not delete test users with ID 1, 2, 3, 4 or 5.",
                responseDelete.htmlPath().getString("html.body"),
                "Not expected <body> result");
    }

    @Test
    public void deleteAuthUserTest() {
        //GENERATE USER & LOGIN
        Map<String, String> userData = DataGenerate.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);
        this.userInOnAuth = this.getIntFromJson(responseCreateAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData);
        this.header =  this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");

        // DELETE USER
        Response responseDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + this.userInOnAuth,
                this.header,
                this.cookie);

        // GET USER
        Response responseDelUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + this.userInOnAuth,
                this.header,
                this.cookie);

        assertEquals("User not found",
                responseDelUserData.htmlPath().getString("html.body"),
                "Not expected <body> result");

    }
    @Test
    public void deleteAnotherUserTest() {
        //GENERATE User1 & LOGIN
        Map<String, String> userData = DataGenerate.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData);
        int newUserId = this.getIntFromJson(responseCreateAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData);
        this.header =  this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GENERATE User2
        Map<String, String> userData2 = DataGenerate.getRegistrationData();
        Response responseCreateAuth2 = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user", userData2);
        int newUserId2 = this.getIntFromJson(responseCreateAuth2, "id");

        // DELETE User2 AUTH User1 (!!!No error message!!!)
        Response responseDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + newUserId2,
                this.header,
                this.cookie);

        // CHECK User1
        Response responseDelUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + newUserId,
                this.header,
                this.cookie);

        // User1 was deleted
        assertEquals("User not found",
                responseDelUserData.htmlPath().getString("html.body"),
                "Not expected <body> result");

        // CHECK User2
        Response responseDelUserData2 = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + newUserId2,
                this.header,
                this.cookie);

        // User2 was not deleted
        Assertions.assertJsonByName(responseDelUserData2, "username", "learnqa");
    }
}
