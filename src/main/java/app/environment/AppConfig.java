package app.environment;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;

/**
 *
 * @author OAK
 *
 */
@Component
public class AppConfig {

    @Bean
    public ThreadFactory getNamedThreadFactory(){
        return new ThreadFactoryBuilder().setNameFormat("elasticsearch-pool-%d").build();
    }

}
