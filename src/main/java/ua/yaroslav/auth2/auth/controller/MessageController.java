package ua.yaroslav.auth2.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final RabbitTemplate template;

    public MessageController(RabbitTemplate template) {
        this.template = template;
    }

    @GetMapping("/send/{message}")
    String postMessage(@PathVariable("message") String message) {
        logger.info("Send to exchange: " + message);
        template.setExchange("exchange");
        template.convertAndSend(message);
        return "Send to exchange -> " + message;
    }
}