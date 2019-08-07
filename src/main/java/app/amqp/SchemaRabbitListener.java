package app.amqp;

import app.enums.ElasticsearchIndexType;
import app.service.job.SyncJobService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 * Rabbit MQ listener according to queue binding(queue name and exchange name and route key name).
 *
 * @author OAK
 *
 * @since 1.0
 *
 */
@Component
public class SchemaRabbitListener {

    private final static Logger logger = LoggerFactory.getLogger(SchemaRabbitListener.class);

//    @Autowired
//    @Qualifier("costJob")
//    SyncJobService syncJobService;

    @RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value="${spring.rabbitmq.template.queue}", durable = "true", exclusive = "false", autoDelete = "false"),
            exchange=@Exchange(value="${spring.rabbitmq.template.exchange}",type= ExchangeTypes.DIRECT),
            key="${spring.rabbitmq.template.routing-key}"
    ))
    @RabbitHandler
    public void process(@Payload Message message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel)throws Exception {
        logger.info(new Date() + ": " + new String(message.getBody(), "UTF-8"));
        JSONObject jsonObject = JSON.parseObject(new String(message.getBody(), "UTF-8"));
        String schema = jsonObject.getString("schema");
        String version = jsonObject.getString("version");
        //syncJobService.run(schema, ElasticsearchIndexType.INDEX, version);
        channel.basicAck(deliveryTag,false);
    }

}
