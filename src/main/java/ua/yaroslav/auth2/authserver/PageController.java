package ua.yaroslav.auth2.authserver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/auth")
    public String getLogin(){
        return "login";
    }
}