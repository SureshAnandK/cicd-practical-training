package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Tag("api")
class ApiSmokeTest {

    @BeforeAll
    static void configure() {
        RestAssured.baseURI = System.getProperty("baseUrl", "http://localhost:8080");
    }

    @Test
    @DisplayName("API-001: GET /health returns 200 and status UP")
    void healthEndpointShouldReturnSuccess() {
        given()
            .when().get("/health")
            .then().statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Test
    @DisplayName("API-002: GET /api/products returns 200 and a valid list")
    void productsEndpointShouldReturnValidStructure() {
        given()
            .when().get("/api/products")
            .then().statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(greaterThan(0)))
            .body("[0].id", notNullValue())
            .body("[0].name", notNullValue())
            .body("[0].price", notNullValue());
    }

    @Test
    @DisplayName("API-003: POST /api/login with invalid credentials returns an error")
    void loginWithInvalidCredentialsShouldFail() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"wrong\",\"password\":\"wrong\"}")
            .when().post("/api/login")
            .then().statusCode(401)
            .body("error", equalTo("Invalid credentials"));
    }

    @Test
    @DisplayName("API-004: Invalid endpoint returns 404")
    void invalidEndpointShouldReturnNotFound() {
        given()
            .when().get("/api/does-not-exist")
            .then().statusCode(404);
    }
}
