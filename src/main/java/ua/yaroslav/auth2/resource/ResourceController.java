package ua.yaroslav.auth2.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.exception.AccessTokenBase64DecodeException;
import ua.yaroslav.auth2.auth.exception.AccessTokenHasExpiredException;
import ua.yaroslav.auth2.auth.exception.AccessTokenInvalidException;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.auth.token.Validator;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.entity.AuthCode;
import ua.yaroslav.auth2.entity.Client;
import ua.yaroslav.auth2.store.iface.ClientStore;
import ua.yaroslav.auth2.store.iface.CodeStore;
import ua.yaroslav.auth2.store.iface.TokenStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

@RestController
public class ResourceController {
    private final static Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final JSONUtil util;
    private final ClientStore clientStore;
    private final CodeStore codeStore;
    private final TokenStore tokenStore;
    private final Validator validator;


    public ResourceController(JSONUtil util, ClientStore clientStore, CodeStore codeStore,
                              TokenStore tokenStore, Validator validator) {
        this.util = util;
        this.clientStore = clientStore;
        this.codeStore = codeStore;
        this.tokenStore = tokenStore;
        this.validator = validator;
    }


    @GetMapping(value = {"/private"})
    public ResponseEntity<String> getPrivateData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Private Resource was requested");
        try {
            validator.validate(request);
        } catch (AccessTokenHasExpiredException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(e.toJSON());
        } catch (AccessTokenInvalidException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(e.toJSON());
        } catch (AccessTokenBase64DecodeException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(e.toJSON());
        }

        StringBuilder builder = new StringBuilder();
        writeHeaders(builder, request);
        return ResponseEntity.ok().body(builder.toString());
    }

    private void writeHeaders(StringBuilder builder, HttpServletRequest request) {
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            if (value.length() < 60)
                builder.append(key).append(" -> [").append(value).append("]").append("<br>\n");
            else
                builder.append(key).append(" -> [").append(value, 0, 60).append("...]").append("<br>\n");
        }
    }

    @GetMapping("/clients")
    public List<Client> clientList() {
        return clientStore.getClients();
    }

    @GetMapping("/tokens")
    public List<AccessToken> tokenList() {
        return tokenStore.getTokens();
    }

    @GetMapping("/codes")
    public List<AuthCode> codeList() {
        return codeStore.getCodes();
    }
}