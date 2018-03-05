package ua.yaroslav.auth2.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.json.JSONUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@RestController
public class ResourceController {
    private final JSONUtil util;
    private final static Logger logger = LoggerFactory.getLogger(ResourceController.class);


    public ResourceController(JSONUtil util) {
        this.util = util;
    }


    @GetMapping(value = {"/private"})
    public String getPrivateData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Private Resource was requested");
        logger.info("Header: " + request.getHeader("Authorization"));
        StringBuilder builder = new StringBuilder();

        if (request.getHeader("Authorization") != null) {
            String header = request.getHeader("Authorization");
            header = header.substring(7, header.length());
            AccessToken accessToken = util.readTokenFromB64(header);
            logger.info("Access Token ->");
            logger.info(util.objectToString(accessToken));

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
}