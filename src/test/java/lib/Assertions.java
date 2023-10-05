package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response Response, String name, int expectedResult) {
        Response.then().assertThat().body("$",hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedResult, value, "JSON value is not equal to expected value");
    }
    public static void assertJsonByName(Response Response, String name, String expectedResult) {
        Response.then().assertThat().body("$",hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectedResult, value, "JSON value is not equal to expected value");
    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer) {
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not expected"
        );
    }
    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response statusCode is not expected"
        );
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName) {
            Response.then().body("$", hasKey(expectedFieldName));
    }

    public static void assertJsonHasFields(Response Response, String[] expectedFieldNames) {
        for(String expectedFieldName : expectedFieldNames){
            assertJsonHasField(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotField(Response Response, String notExpectedFieldName) {
        Response.then().body("$", not(hasKey(notExpectedFieldName)));
    }

    public static void assertJsonHasNotFields(Response Response, String[] notExpectedFieldNames) {
        for(String notExpectedFieldName : notExpectedFieldNames){
            assertJsonHasNotField(Response, notExpectedFieldName);
        }
    }
}
