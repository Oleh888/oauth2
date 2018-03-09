package ua.yaroslav.auth2;

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
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.entity.Client;
import ua.yaroslav.auth2.store.PostgreClientStore;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class AppRunnerTests {
    private final String client = "test";
    private final String secret = "test";

    @Autowired
    private PostgreClientStore clientStore;

    @Before
    public void setUp() {
        clientStore.saveClient(new Client(client, secret));
    }

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JSONUtil util;

    @Test
    public void getTokenFromCodeWhenClientDataIsCorrectThenTokenValid() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(client));
        params.put("client_secret", Collections.singletonList(secret));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));

        ResponseEntity<String> response = this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(response.getBody(), TokenResponseDto.class);
        AccessToken accessToken = util.readTokenFromB64(token.getAccess_token());

        assertEquals(token.getToken_type(), "bearer");
        assertEquals(accessToken.getScope(), "read");
        assertEquals(accessToken.getClientID(), client);
        assertThat(accessToken.getTime()).isGreaterThanOrEqualTo(System.currentTimeMillis());
    }

    @Test
    public void getPrivateDataWhenAccessTokenIsValidThenAccessGranted() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(client));
        params.put("client_secret", Collections.singletonList(secret));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));
        ResponseEntity<String> r = this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(r.getBody(), TokenResponseDto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.getAccess_token());
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange("/private", HttpMethod.GET, entity, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody()).contains("host");
    }

    @Test
    public void getTokenFromCodeWhenClientDataIsNotCorrectThenThrowInvalideClientIDException() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList("client2"));
        params.put("client_secret", Collections.singletonList(secret));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));

        ResponseEntity<String> response = this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(response.getBody(), TokenResponseDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("invalid_client_id");
    }

    @Test
    public void getPrivateDataWhenAccessTokenIsNotValidThenThrowAccessTokenDecodeException() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(client));
        params.put("client_secret", Collections.singletonList(secret));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));
        ResponseEntity<String> r = this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(r.getBody(), TokenResponseDto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "123123");
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange("/private", HttpMethod.GET, entity, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("access_token_base64_decode_exception");
    }

    @Test
    public void getPrivateDataWhenAccessTokenNotPresentThenThrowInvalidAccessTokenException() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange("/private", HttpMethod.GET, entity, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("invalid_access_token");
    }

    private String getCode() {
        return util.encodeObject(util.getCode(new AuthRequestDto
                ("login", "pass", client, "code", "uri", "read")
        ));
    }
}