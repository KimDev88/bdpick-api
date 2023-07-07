package com.bdpick;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

@RequiredArgsConstructor
public class BdPickApiApplication {
    public static void main(String[] args) {

        SpringApplication.run(BdPickApiApplication.class, args);
    }

}