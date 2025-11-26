package com.j0aoarthur.iotsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotSimulatorApplication.class, args);
    }

}
