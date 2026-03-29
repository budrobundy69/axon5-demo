package com.example.demo.account.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void accountFlowWorksEndToEnd() throws Exception {
        String accountId = "acc-it-1";

        HttpResponse<String> openResponse = post("/accounts", "{\"accountId\":\"" + accountId + "\",\"initialBalance\":100}");
        assertEquals(HttpStatus.ACCEPTED.value(), openResponse.statusCode());

        HttpResponse<String> depositResponse = post("/accounts/" + accountId + "/deposits", "{\"amount\":50}");
        assertEquals(HttpStatus.ACCEPTED.value(), depositResponse.statusCode());

        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            HttpResponse<String> accountResponse = get("/accounts/" + accountId);
            assertEquals(HttpStatus.OK.value(), accountResponse.statusCode());

            JsonNode body = objectMapper.readTree(accountResponse.body());
            if (body.has("balance") && body.get("balance").asLong() == 150) {
                return;
            }
            Thread.sleep(50);
        }

        fail("Account balance was not projected to 150 within timeout");
    }

    @Test
    void rejectsInvalidDepositRequest() throws Exception {
        String accountId = "acc-it-2";

        HttpResponse<String> openResponse = post("/accounts", "{\"accountId\":\"" + accountId + "\",\"initialBalance\":0}");
        assertEquals(HttpStatus.ACCEPTED.value(), openResponse.statusCode());

        HttpResponse<String> invalidDepositResponse = post("/accounts/" + accountId + "/deposits", "{\"amount\":0}");
        assertEquals(HttpStatus.BAD_REQUEST.value(), invalidDepositResponse.statusCode());
    }

    private HttpResponse<String> post(String path, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
