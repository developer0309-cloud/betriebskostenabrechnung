package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class MietvertragResourceTest {
    @Test
    void vertragErfasstVorauszahlungJeJahr() {
        String token = token();
        int personId = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"vorname\":\"Ada\",\"nachname\":\"Beispiel\"}")
                .when()
                .post("/api/personen")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
        int mieterId = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"personIds\":[" + personId + "]}")
                .when()
                .post("/api/mieter")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        String vertragId = null;
        try {
            vertragId = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(vertrag(mieterId, """
                            [{"jahr":2026,"betrag":480.00},{"jahr":2027,"betrag":240.50}]
                            """))
                    .when()
                    .post("/api/mietvertraege")
                    .then()
                    .statusCode(200)
                    .body("vorauszahlungen", hasSize(2))
                    .body("vorauszahlungen[0].jahr", equalTo(2026))
                    .body("vorauszahlungen[0].betrag", equalTo(480.00f))
                    .body("vorauszahlungen[1].jahr", equalTo(2027))
                    .body("vorauszahlungen[1].betrag", equalTo(240.50f))
                    .body(not(containsString("kaltmiete")))
                    .extract()
                    .path("id");

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(vertrag(mieterId, """
                            [{"jahr":2026,"betrag":480.00},{"jahr":2026,"betrag":10.00}]
                            """))
                    .when()
                    .put("/api/mietvertraege/" + vertragId)
                    .then()
                    .statusCode(400);

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(vertrag(mieterId, """
                            [{"jahr":2025,"betrag":10.00}]
                            """))
                    .when()
                    .put("/api/mietvertraege/" + vertragId)
                    .then()
                    .statusCode(400);

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(vertrag(mieterId, """
                            [{"jahr":2026,"betrag":500.00}]
                            """))
                    .when()
                    .put("/api/mietvertraege/" + vertragId)
                    .then()
                    .statusCode(200)
                    .body("vorauszahlungen", hasSize(1))
                    .body("vorauszahlungen[0].jahr", equalTo(2026))
                    .body("vorauszahlungen[0].betrag", equalTo(500.00f));
        } finally {
            if (vertragId != null) {
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/api/mietvertraege/" + vertragId)
                        .then()
                        .statusCode(204);
            }
        }
    }

    private static String vertrag(int mieterId, String vorauszahlungen) {
        return """
                {
                  "mieteinheitId": 1,
                  "mieterId": %d,
                  "vertragsbeginn": "2026-01-01",
                  "vertragsende": "2027-12-31",
                  "vorauszahlungen": %s
                }
                """.formatted(mieterId, vorauszahlungen);
    }

    private static String token() {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"benutzername\":\"eigentuemer\",\"passwort\":\"bitte-aendern\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
