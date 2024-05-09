package com.tekcapzule.mediaconverter.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tekcapzule.mediaconverter","com.tekcapzule.core"})
public class MediaConverterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaConverterApplication.class, args);
    }
}
