package com.buildsmart.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class IamServiceApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }

}
