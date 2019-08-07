package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * application for bootstrap class.
 *
 * @author OAK
 *
 * @since 2019/07/10 16:47:00 PM.
 *
 */
@SpringBootApplication(scanBasePackages = {"app"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}