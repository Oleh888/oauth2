package ua.yaroslav.auth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class Auth2Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Auth2Application.class, args);
    }
}