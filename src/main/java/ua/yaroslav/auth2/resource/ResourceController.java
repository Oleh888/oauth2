package ua.yaroslav.auth2.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.entity.AccessToken;
import ua.yaroslav.auth2.auth.entity.AuthCode;
import ua.yaroslav.auth2.auth.entity.Client;
import ua.yaroslav.auth2.auth.service.JSONUtil;
import ua.yaroslav.auth2.auth.service.ValidationService;
import ua.yaroslav.auth2.auth.store.iface.ClientStore;
import ua.yaroslav.auth2.auth.store.iface.CodeRepository;
import ua.yaroslav.auth2.auth.store.iface.TokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@RestController
public class ResourceController {
    private final static Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final JSONUtil util;
    private final ClientStore clientStore;
    private final TokenRepository tokenRepository;
    private final CodeRepository codeRepository;
    private final ValidationService validator;


    public ResourceController(JSONUtil util, ClientStore clientStore,
                              TokenRepository tokenRepository, CodeRepository codeRepository, ValidationService validator) {
        this.util = util;
        this.clientStore = clientStore;
        this.tokenRepository = tokenRepository;
        this.codeRepository = codeRepository;
        this.validator = validator;
    }


    @GetMapping(value = {"/private"})
    public ResponseEntity<String> getPrivateData(HttpServletRequest request) {
        logger.info("Private Resource was requested");
        validator.validate(request);
        return ResponseEntity.ok().body(writeHeaders(request));
    }

    @GetMapping("/clients")
    public List<Client> clientList() {
        return clientStore.getClients();
    }

    @GetMapping("/tokens")
    public List<AccessToken> tokenList() {
        return (List<AccessToken>) tokenRepository.findAll();
    }

    @GetMapping("/codes")
    public List<AuthCode> codeList() {
        return (List<AuthCode>) codeRepository.findAll();
    }

    private String writeHeaders(HttpServletRequest request) {
        Enumeration headerNames = request.getHeaderNames();
        StringBuilder result = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);

            if (value.length() < 60)
                result.append(key).append(" -> [").append(value).append("]").append("<br>\n");
            else
                result.append(key).append(" -> [").append(value, 0, 60).append("...]").append("<br>\n");
        }
        return result.toString();
    }
}