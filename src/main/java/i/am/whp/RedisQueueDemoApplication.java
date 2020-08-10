package i.am.whp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "i.am.whp.controller",
        "i.am.whp.config",
        "i.am.whp.handler",
        "i.am.whp.manager"
})
public class RedisQueueDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisQueueDemoApplication.class, args);
    }
}
