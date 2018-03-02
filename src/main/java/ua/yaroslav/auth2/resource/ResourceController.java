package ua.yaroslav.auth2.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.json.JSONUtil;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.store.InMemoryStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class ResourceController {
    private final InMemoryStore inMemoryStore;
    private final static Logger logger = LoggerFactory.getLogger(ResourceController.class);


    public ResourceController(InMemoryStore inMemoryStore) {
        this.inMemoryStore = inMemoryStore;
    }


    @GetMapping(value = {"/private"})
    public String getPrivateData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Private Resource was requested");
        StringBuilder builder = new StringBuilder();

        if (request.getHeader("Authorization") != null && request.getHeader("Authorization").length() > 7) {
            String tokenFromRequest = request.getHeader("Authorization");

            tokenFromRequest = tokenFromRequest.substring(7, tokenFromRequest.length());
            AccessToken accessToken = JSONUtil.readTokenFromB64(tokenFromRequest);
            logger.info("Access Token ->");
            logger.info(JSONUtil.objectToString(accessToken));

            if (accessToken.getExpiresIn() > System.currentTimeMillis()) {
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

    @GetMapping(value = {"/codes"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CopyOnWriteArrayList<AuthCode> getCodes() {
        return inMemoryStore.getCodes();
    }

    @GetMapping(value = {"/tokens"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CopyOnWriteArrayList<AccessToken> getTokens() {
        return inMemoryStore.getTokens();
    }

    @GetMapping("/")
    public String homePage() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\"/>\n" +
                "    <title>Page you are not looking for</title>\n" +
                "    <link rel=\"stylesheet\" href=\"/css/main.css\" />" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"main-container\">\n" +
                "    <p>This is not the page you are looking for...</p>\n" +
                "    <p>Try to <a href=\"auth\">authenticate</a></p>" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }
}