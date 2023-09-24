package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void asserJsonByName(Response Response, String name, int expectedResult) {
        Response.then().assertThat().body("$",hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedResult, value, "JSON value is not equal to expected value");
    }
}
