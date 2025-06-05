package Gotcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "Gotcha",
        "gotcha_user",
        "gotcha_common",
        "gotcha_auth",
        "gotcha_domain",
        "socket_server"
})
@EnableJpaRepositories(basePackages = {
        "gotcha_user.repository",
        "Gotcha.domain"
})
@EntityScan(basePackages = {
        "gotcha_domain",
        "gotcha_user"
})
@EnableScheduling
public class GotchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GotchaApplication.class, args);
    }

}
