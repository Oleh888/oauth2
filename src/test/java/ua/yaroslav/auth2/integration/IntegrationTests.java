package ua.yaroslav.auth2.integration;

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
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.service.JSONUtil;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.Client;
import ua.yaroslav.auth2.auth.store.PostgresClientStore;

import java.io.IOException;
import java.util.Collections;

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
    @Autowired
    private JSONUtil util;

    @Before
    public void setUp() {
        if (clientStore.getClients().size() == 0)
            clientStore.saveClient(new Client(CLIENT, SECRET));
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
        AccessToken accessToken = util.readTokenFromB64(token.getAccessToken());

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
        return util.encodeObject(util.getCode(new AuthRequestDto
                ("login", "pass", CLIENT, "code", "uri", SCOPE)
        ));
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
}