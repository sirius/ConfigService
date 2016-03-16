package no.cantara.cs.client;

import com.jayway.restassured.http.ContentType;
import no.cantara.cs.dto.CheckForUpdateRequest;
import no.cantara.cs.testsupport.TestServer;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;

public class CheckForUpdateTest {

    private ConfigServiceClient configServiceClient;

    private TestServer testServer;

    @BeforeClass
    public void startServer() throws Exception {
        testServer = new TestServer();
        testServer.cleanAllData();
        testServer.start();
        configServiceClient = testServer.getConfigServiceClient();
    }

    @AfterClass
    public void stop() {
        if (testServer != null) {
            testServer.stop();
        }
        configServiceClient.cleanApplicationState();
    }

    @Test
    public void testCheckForUpdateBrokenJson() {
        given()
                .auth().basic(TestServer.USERNAME, TestServer.PASSWORD)
                .contentType(ContentType.JSON)
                .body("{broken json}")
                .log().everything()
                .expect()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .log().ifError()
                .when()
                .post(ClientResource.CLIENT_PATH + "/{clientId}/sync", 1);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCheckForUpdateUnregisteredClient() throws IOException {
        configServiceClient.checkForUpdate("non-existing-client-id", new CheckForUpdateRequest(""));
    }

}