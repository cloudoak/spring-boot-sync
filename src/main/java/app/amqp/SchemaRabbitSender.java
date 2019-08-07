package app.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * Schema rabbit sender.
 *
 * @author OAK
 *
 */
@Component
public class SchemaRabbitSender {

    private final static Logger logger = LoggerFactory.getLogger(SchemaRabbitSender.class);

    /**
     * Rabbit template.
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Rabbit template exchange.
     */
    @Value("${spring.rabbitmq.template.exchange}")
    private volatile String exchange;

    /**
     * Rabbit template routing key.
     */
    @Value("${spring.rabbitmq.template.routing-key}")
    private volatile String routingKey;

}
