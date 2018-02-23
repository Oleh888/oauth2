package ua.yaroslav.auth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import java.util.Properties;

@SpringBootApplication
public class Auth2Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Auth2Application.class);
        //Properties properties = new Properties();
        //properties.setProperty("spring.resources.static-locations", "classpath:/resources/");
        //app.setDefaultProperties(properties);
        app.run(args);
    }
}