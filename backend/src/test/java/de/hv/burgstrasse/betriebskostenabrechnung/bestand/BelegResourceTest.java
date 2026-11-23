package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class BelegResourceTest {
    @Test
    void belegWirdAngelegtGeaendertUndGeloescht() {
        String token = token();
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/belege/kostenarten")
                .then()
                .statusCode(200)
                .body("$", hasSize(10))
                .body("[0]", equalTo("Grundsteuer"))
                .body("[9]", equalTo("Direkte Kosten"));

        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {"valutadatum":"2026-03-15","betrag":123.45,"beschreibung":" Grundsteuer 2026 ","kostenart":"Grundsteuer","kennzeichen":"indirekt"}
                        """)
                .when()
                .post("/api/belege")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("valutadatum", equalTo("2026-03-15"))
                .body("betrag", equalTo(123.45f))
                .body("beschreibung", equalTo("Grundsteuer 2026"))
                .body("kostenart", equalTo("Grundsteuer"))
                .body("kennzeichen", equalTo("indirekt"))
                .body("mieterId", equalTo(null))
                .body("mieterName", equalTo(null))
                .extract()
                .path("id");

        try {
            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body("""
                            {"valutadatum":"2026-04-01","betrag":10.00,"kostenart":"Unbekannt","kennzeichen":"indirekt"}
                            """)
                    .when()
                    .put("/api/belege/" + id)
                    .then()
                    .statusCode(400);

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body("""
                            {"valutadatum":"2026-04-01","betrag":10.00,"kostenart":"Direkte Kosten","kennzeichen":"indirekt"}
                            """)
                    .when()
                    .put("/api/belege/" + id)
                    .then()
                    .statusCode(400);

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body("""
                            {"valutadatum":"2026-04-01","betrag":99.00,"beschreibung":"","kostenart":"Allgemeinstrom","kennzeichen":"indirekt"}
                            """)
                    .when()
                    .put("/api/belege/" + id)
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(id))
                    .body("valutadatum", equalTo("2026-04-01"))
                    .body("betrag", equalTo(99.00f))
                    .body("beschreibung", equalTo(null))
                    .body("kostenart", equalTo("Allgemeinstrom"))
                    .body("kennzeichen", equalTo("indirekt"))
                    .body("mieterId", equalTo(null));
        } finally {
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/api/belege/" + id)
                    .then()
                    .statusCode(204);
        }
    }

    @Test
    void direkteKostenBrauchenMieterUndKostenart() {
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

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {"valutadatum":"2026-05-01","betrag":12.00,"kostenart":"Direkte Kosten","kennzeichen":"direkt"}
                        """)
                .when()
                .post("/api/belege")
                .then()
                .statusCode(400);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {"valutadatum":"2026-05-01","betrag":12.00,"kostenart":"Grundsteuer","kennzeichen":"direkt","mieterId":%d}
                        """.formatted(mieterId))
                .when()
                .post("/api/belege")
                .then()
                .statusCode(400);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {"valutadatum":"2026-05-01","betrag":12.00,"kostenart":"Grundsteuer","kennzeichen":"indirekt","mieterId":%d}
                        """.formatted(mieterId))
                .when()
                .post("/api/belege")
                .then()
                .statusCode(400);

        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {"valutadatum":"2026-05-01","betrag":12.00,"beschreibung":"Reparatur","kostenart":"Direkte Kosten","kennzeichen":"direkt","mieterId":%d}
                        """.formatted(mieterId))
                .when()
                .post("/api/belege")
                .then()
                .statusCode(200)
                .body("kostenart", equalTo("Direkte Kosten"))
                .body("kennzeichen", equalTo("direkt"))
                .body("mieterId", equalTo(mieterId))
                .body("mieterName", equalTo("Ada Beispiel"))
                .extract()
                .path("id");

        try {
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/api/belege")
                    .then()
                    .statusCode(200)
                    .body("find { it.id == " + id + " }.mieterId", equalTo(mieterId))
                    .body("find { it.id == " + id + " }.mieterName", equalTo("Ada Beispiel"));

            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/api/mieter/" + mieterId)
                    .then()
                    .statusCode(409);

            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body("""
                            {"valutadatum":"2026-05-02","betrag":8.00,"kostenart":"Hausreinigung","kennzeichen":"indirekt"}
                            """)
                    .when()
                    .put("/api/belege/" + id)
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(id))
                    .body("kennzeichen", equalTo("indirekt"))
                    .body("kostenart", equalTo("Hausreinigung"))
                    .body("mieterId", equalTo(null));
        } finally {
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/api/belege/" + id)
                    .then()
                    .statusCode(204);
        }
    }

    @Test
    void belegeSindNachIdSortiert() {
        String token = token();
        Integer ersteId = beleg(token, "2026-01-01");
        Integer zweiteId = beleg(token, "2026-12-31");
        try {
            List<Integer> ids = given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/api/belege")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("id");
            assertTrue(ids.indexOf(ersteId) < ids.indexOf(zweiteId));
        } finally {
            loeschen(token, ersteId);
            loeschen(token, zweiteId);
        }
    }

    private static Integer beleg(String token, String valutadatum) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {"valutadatum":"%s","betrag":1.00,"kostenart":"Grundsteuer","kennzeichen":"indirekt"}
                        """.formatted(valutadatum))
                .when()
                .post("/api/belege")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    private static void loeschen(String token, Integer id) {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/belege/" + id)
                .then()
                .statusCode(204);
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
