package ua.yaroslav.auth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.entity.Client;
import ua.yaroslav.auth2.store.PostgreClientStore;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class InMemoryDatabaseTest {
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
    public void getTokenFromCodeAndCheckFieldsWhenClientDataIsCorrect() throws IOException {
        System.out.println("\nClients:");
        System.out.println(clientStore.getClients() + "\n");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(client));
        params.put("client_secret", Collections.singletonList(secret));
        params.put("grant_type", Collections.singletonList("authorization_code"));
        params.put("code", Collections.singletonList(getCode()));
        params.put("scope", Collections.singletonList("read"));

        ResponseEntity<String> response = this.restTemplate.postForEntity("/token", params, String.class);
        TokenResponseDto token = mapper.readValue(response.getBody(), TokenResponseDto.class);

        assertThat(response.getBody()).contains("access_token");
    }

    private String getCode() {
        return util.encodeObject(util.getCode(new AuthRequestDto
                ("login", "pass", client, "code", "uri", "read")
        ));
    }
}