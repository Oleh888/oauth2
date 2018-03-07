package ua.yaroslav.auth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.json.JSONUtil;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Auth2ApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JSONUtil util;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void getTokenFromCodeAndCheckFieldsWhenClientDataIsCorrect() throws IOException {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList("client"));
        params.put("client_secret", Collections.singletonList("secret"));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));

        //when
        ResponseEntity<String> response =
                this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(response.getBody(), TokenResponseDto.class);

        //then
        assertThat(response.getBody()).contains("access_token");
        assertThat(response.getBody()).contains("refresh_token");
        assertThat(response.getBody()).contains("expires_in");
        assertEquals(token.getToken_type(), "bearer");
    }

    @Test
    public void getPrivateDataWhenAccessTokenIsValid() throws IOException {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList("client"));
        params.put("client_secret", Collections.singletonList("secret"));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));
        ResponseEntity<String> r = this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(r.getBody(), TokenResponseDto.class);

        //when
        System.out.println("Token[pr]: " +  token.getAccess_token());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " +  token.getAccess_token());
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange("/private", HttpMethod.GET, entity, String.class);

        //then
        System.out.println(response);
        assertEquals(response.getStatusCode(),HttpStatus.OK);
        assertThat(response.getBody()).contains("host");
    }

    @Test
    public void getTokenFromCodeAndCheckFieldsWhenClientDataIsNotCorrect() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList("client2"));
        params.put("client_secret", Collections.singletonList("secret"));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));

        ResponseEntity<String> response =
                this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(response.getBody(), TokenResponseDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("invalid_client_id");
    }

    @Test
    public void getPrivateDataWhenAccessTokenIsNotValid() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList("client"));
        params.put("client_secret", Collections.singletonList("secret"));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));
        ResponseEntity<String> r = this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(r.getBody(), TokenResponseDto.class);

        System.out.println("Token[pr]: " +  token.getAccess_token());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " +  "123123");
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange("/private", HttpMethod.GET, entity, String.class);

        System.out.println(response);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("invalid_access_token");
    }

    private String getCode() {
        return util.encodeObject(util.getCode(new AuthRequestDto
                ("login", "pass", "client", "code", "uri", "read")
        ));
    }
}