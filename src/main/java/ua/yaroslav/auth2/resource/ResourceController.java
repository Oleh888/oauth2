package ua.yaroslav.auth2.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.entity.AccessToken;
import ua.yaroslav.auth2.auth.json.JSONUtil;
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


    public ResourceController(JSONUtil util, ClientStore clientStore, CodeStore codeStore, TokenStore tokenStore) {
        this.util = util;
        this.clientStore = clientStore;
        this.codeStore = codeStore;
        this.tokenStore = tokenStore;
    }


    @GetMapping(value = {"/private"})
    public String getPrivateData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Private Resource was requested");
        logger.info("Header: [" + request.getHeader("Authorization") + "]");

        StringBuilder builder = new StringBuilder();
        if (request.getHeader("Authorization") != null) {
            String header = request.getHeader("Authorization");
            header = header.substring(7, header.length());
            AccessToken accessToken = util.readTokenFromB64(header);
            logger.info("Access Token ->");
            logger.info(util.objectToString(accessToken));

            if (accessToken.getTime() > System.currentTimeMillis()) {
                Enumeration headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String key = (String) headerNames.nextElement();
                    String value = request.getHeader(key);
                    if (value.length() < 60)
                        builder.append(key).append(" -> [").append(value).append("]").append("<br>\n");
                    else
                        builder.append(key).append(" -> [").append(value, 0, 110).append("...]").append("<br>\n");
                }
            } else builder.append("<h3>Access Token is no longer valid</h3>\n");
        } else {
            response.setStatus(401);
            builder.append("<h3>Access not granted!</h3>\n");
        }

        return builder.toString();
    }

    @GetMapping("/clients")
    public List<Client> clientList(){
        return clientStore.getClients();
    }

    @GetMapping("/tokens")
    public List<AccessToken> tokenList(){
        return tokenStore.getTokens();
    }

    @GetMapping("/codes")
    public List<AuthCode> codeList(){
        return codeStore.getCodes();
    }

    private boolean checkAuth(){
        return false;
    }
}