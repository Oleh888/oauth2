package ua.yaroslav.auth2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.entity.AccessToken;

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
    private String at;

    @Test
    public void getTokenFromCodeAndCheckFields() throws IOException {
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

        //then
        assertThat(response.getBody()).contains("access_token");
        assertThat(response.getBody()).contains("refresh_token");
        assertThat(response.getBody()).contains("expires_in");

        System.out.println("response.getBody():");
        System.out.println(response.getBody());
        AccessToken token = util.readTokenFromB64(response.getBody());
        assertEquals(token.getType(), "bearer");

        at = response.getBody();
        System.out.println(token);
    }

    @Test
    public void getPrivateData() {
        System.out.println(at);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + at);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange("/private", HttpMethod.GET, entity, String.class);
        System.out.println(response);
    }

    private String getCode() {
        return util.encodeObject(util.getCode(new AuthRequestDto
                ("login", "pass", "client", "code", "uri", "read")
        ));
    }
}