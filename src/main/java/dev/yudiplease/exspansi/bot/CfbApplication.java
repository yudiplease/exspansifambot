package dev.yudiplease.exspansi.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CfbApplication {

    public static void main(String[] args) {
        SpringApplication.run(CfbApplication.class, args);
    }

}
