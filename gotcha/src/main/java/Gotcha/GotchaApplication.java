package Gotcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"Gotcha", "socket_server"})
public class GotchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GotchaApplication.class, args);
    }

}
