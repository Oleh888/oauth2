package ua.yaroslav.auth2.auth.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@EnableRabbit
@Service
public class RabbitMqListener {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqListener.class);

    @RabbitListener(queues = "main")
    public void worker1(String message) {
        logger.info("accepted on worker 1 (main) : " + message);
    }

    @RabbitListener(queues = "query")
    public void worker2(String message) {
        logger.info("accepted on worker 2 (query): " + message);
    }
}