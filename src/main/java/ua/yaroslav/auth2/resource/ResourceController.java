package ua.yaroslav.auth2.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yaroslav.auth2.auth.entity.Client;
import ua.yaroslav.auth2.auth.service.token.ValidationService;
import ua.yaroslav.auth2.auth.store.ClientStore;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@RestController
public class ResourceController {
    private final static Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final ClientStore clientStore;
    private final ValidationService validationService;


    public ResourceController(ClientStore clientStore, ValidationService validator) {
        this.clientStore = clientStore;
        this.validationService = validator;
    }


    @GetMapping(value = {"/private"})
    public ResponseEntity<String> getPrivateData(HttpServletRequest request) {
        logger.info("Private Resource was requested");
        validationService.validate(request);
        return ResponseEntity.ok().body(writeHeaders(request));
    }

    @GetMapping("/clients")
    public List<Client> clientList() {
        return clientStore.getClients();
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