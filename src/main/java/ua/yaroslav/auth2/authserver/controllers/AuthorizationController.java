package ua.yaroslav.auth2.authserver.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.yaroslav.auth2.authserver.dto.AuthRequest;
import ua.yaroslav.auth2.authserver.json.JSONUtil;
import ua.yaroslav.auth2.authserver.json.entity.AuthCode;
import ua.yaroslav.auth2.store.InMemoryStore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthorizationController {
    @Value("${client.id}")
    private String CLIENT_ID;
    @Value("${client.secret}")
    private String CLIENT_SECRET;

    private final JSONUtil jsonUtil;
    private final InMemoryStore store;


    public AuthorizationController(JSONUtil jsonUtil, InMemoryStore store) {
        this.jsonUtil = jsonUtil;
        this.store = new InMemoryStore();
    }


    @PostMapping(value = {"/auth"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void getCode(AuthRequest authRequest, HttpServletResponse response) throws IOException {
        System.out.println("\n--------------------get-code-invocation---------------------\n");
        System.out.println("client_id: \n\t" + authRequest.getClientID());
        System.out.println("response_type: \n\t" + authRequest.getResponseType());
        System.out.println("username: \n\t" + authRequest.getUsername());
        System.out.println("password: \n\t" + authRequest.getPassword());
        System.out.println("scope: \n\t" + "[" + authRequest.getScope() + "]\n");

        if (authRequest.getClientID().equals(CLIENT_ID)) {
            if (authRequest.getResponseType().equals("code")) {
                AuthCode code = jsonUtil.getCode(authRequest);
                store.addCode(code);
                String url = "https://developers.google.com/oauthplayground?" +
                        "code=" + jsonUtil.encodeObject(code) + "&" +
                        "state=markOne";
                response.sendRedirect(url);
            }
        }
    }

    @GetMapping("/")
    @ResponseBody
    public String getHome() {
        return "<h3>This is not the page you are looking for</h3>";
    }

    @GetMapping("/auth")
    public String getLogin(Model model) {
        model.addAttribute("redirect_uri", "redirect_uri");
        return "login";
    }
}