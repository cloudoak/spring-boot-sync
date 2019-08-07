package app.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * Schema rabbit confirm call back.
 *
 * @author OAK
 * @since 2019/07/23 09:42:00 PM.
 *
 */
@Component
public class SchemaRabbitConfirmCallback implements RabbitTemplate.ConfirmCallback  {

    private final static Logger logger = LoggerFactory.getLogger(SchemaRabbitConfirmCallback.class);

    /**
     * rabbit template.
     */
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public SchemaRabbitConfirmCallback(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this);
     }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        logger.info("confirm: {}, b: {}, s: {}", correlationData.getId(), b, s);
    }
}