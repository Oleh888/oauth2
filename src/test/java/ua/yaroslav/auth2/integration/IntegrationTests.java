package ua.yaroslav.auth2.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.auth.entity.Client;
import ua.yaroslav.auth2.auth.store.implementation.PostgresClientStore;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class IntegrationTests {
    public static final String CLIENT = "test_client";
    public static final String SECRET = "test_secret";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String SCOPE = "read";
    public static final String BEARER = "bearer";
    public static final String TOKEN_URL = "/token";
    public static final String PRIVATE_RESOURCE_URL = "/private";

    @Autowired
    private PostgresClientStore clientStore;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        if (clientStore.getClients().size() == 0)
            clientStore.saveClient(new Client(CLIENT, SECRET));
        System.out.println("clients: " + clientStore.getClients());
    }

    @Test
    public void postWithAuthCodeShouldReturnValidAccessAndRefreshToken() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(CLIENT));
        params.put("client_secret", Collections.singletonList(SECRET));
        params.put("grant_type", Collections.singletonList(AUTHORIZATION_CODE));
        params.put("code", Collections.singletonList(generateCode()));
        params.put("scope", Collections.singletonList(SCOPE));

        ResponseEntity<String> response = this.restTemplate.postForEntity(TOKEN_URL, params, String.class);
        TokenResponseDto token = mapper.readValue(response.getBody(), TokenResponseDto.class);
        AccessToken accessToken = readTokenFromB64(token.getAccessToken());

        assertEquals(token.getToken_type(), BEARER);
        assertNotNull(token.getRefresh_token());
        assertEquals(accessToken.getScope(), SCOPE);
        assertEquals(accessToken.getClientID(), CLIENT);
        assertThat(accessToken.getTime()).isGreaterThanOrEqualTo(System.currentTimeMillis());
    }

    @Test
    public void getWithAccessTokenShouldReturnRequestHeaders() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + generateAccessToken());
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(PRIVATE_RESOURCE_URL, HttpMethod.GET, entity, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody()).contains("host");
    }

    @Test
    public void postWithAuthCodeAndInvalidClientIDShouldThrowAuthException() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList("client2"));
        params.put("client_secret", Collections.singletonList(SECRET));
        params.put("grant_type", Collections.singletonList(AUTHORIZATION_CODE));
        params.put("code", Collections.singletonList(generateCode()));
        params.put("scope", Collections.singletonList(SCOPE));

        ResponseEntity<String> response = this.restTemplate.postForEntity(TOKEN_URL, params, String.class);
        TokenResponseDto token = mapper.readValue(response.getBody(), TokenResponseDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("invalid_request");
    }

    @Test
    public void getWithInvalidAccessTokenShouldThrowAccessTokenDecodeException() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(CLIENT));
        params.put("client_secret", Collections.singletonList(SECRET));
        params.put("grant_type", Collections.singletonList(AUTHORIZATION_CODE));
        params.put("code", Collections.singletonList(generateCode()));
        params.put("scope", Collections.singletonList(SCOPE));
        ResponseEntity<String> r = this.restTemplate.postForEntity(TOKEN_URL, params, String.class);
        TokenResponseDto token = mapper.readValue(r.getBody(), TokenResponseDto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "123123");
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(PRIVATE_RESOURCE_URL, HttpMethod.GET, entity, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        System.out.println(response.getBody());
        System.out.println();
        assertThat(response.getBody()).contains("server_error");
    }

    @Test
    public void getWithEmptyAccessTokenShouldThrowInvalidAccessTokenException() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(PRIVATE_RESOURCE_URL, HttpMethod.GET, entity, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("access_denied");
    }

    private String generateCode() {
        return encodeObject(new AuthCode(CLIENT, "login", 15 * 1000 * 5));
    }

    private String generateAccessToken() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(CLIENT));
        params.put("client_secret", Collections.singletonList(SECRET));
        params.put("grant_type", Collections.singletonList(AUTHORIZATION_CODE));
        params.put("code", Collections.singletonList(generateCode()));
        params.put("scope", Collections.singletonList(SCOPE));
        ResponseEntity<String> r = this.restTemplate.postForEntity(TOKEN_URL, params, String.class);
        TokenResponseDto token = mapper.readValue(r.getBody(), TokenResponseDto.class);
        return token.getAccessToken();
    }

    private AccessToken readTokenFromB64(String token) throws IOException {
        return mapper.readValue(new String(Base64.getDecoder().decode(token.getBytes())), AccessToken.class);
    }

    private String encodeObject(Object code) {
        return Base64.getEncoder().encodeToString(Objects.requireNonNull(objectToJSON(code)).getBytes());
    }

    private String objectToJSON(Object code) {
        try {
            return "\n" + mapper.writeValueAsString(code) + "\n";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}