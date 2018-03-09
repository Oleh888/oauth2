package ua.yaroslav.auth2.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.yaroslav.auth2.auth.dto.AuthRequestDto;
import ua.yaroslav.auth2.auth.dto.TokenResponseDto;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.store.PostgreClientStore;
import ua.yaroslav.auth2.store.PostgreCodeStore;
import ua.yaroslav.auth2.store.PostgreTokenStore;
import ua.yaroslav.auth2.store.iface.ClientRepository;
import ua.yaroslav.auth2.store.iface.CodeRepository;
import ua.yaroslav.auth2.store.iface.TokenRepository;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FakeDatabaseTest {
    private final String client = "test";
    private final String secret = "test";

    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private CodeRepository codeRepository;
    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private PostgreClientStore clientStore;
    @MockBean
    private PostgreTokenStore tokenStore;
    @MockBean
    private PostgreCodeStore codeStore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(clientStore.checkClient(client)).thenReturn(true);
        when(clientStore.checkClient(client, secret)).thenReturn(true);
    }

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JSONUtil util;

    @Test
    public void getTokenFromCodeAndCheckFieldsWhenClientDataIsCorrect() throws IOException {
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