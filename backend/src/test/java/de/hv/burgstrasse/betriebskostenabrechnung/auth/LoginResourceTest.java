package de.hv.burgstrasse.betriebskostenabrechnung.auth;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class LoginResourceTest {
    @Test
    void eigentuemerErhaeltTokenUndSiehtDenSeed() {
        String token = given()
                .contentType("application/json")
                .body("{\"benutzername\":\"eigentuemer\",\"passwort\":\"bitte-aendern\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/mieteinheiten")
                .then()
                .statusCode(200)
                .body("size()", equalTo(9));

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/mietvertraege")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    void falschesPasswortBleibtAussen() {
        given()
                .contentType("application/json")
                .body("{\"benutzername\":\"eigentuemer\",\"passwort\":\"falsch\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401);
    }
}
