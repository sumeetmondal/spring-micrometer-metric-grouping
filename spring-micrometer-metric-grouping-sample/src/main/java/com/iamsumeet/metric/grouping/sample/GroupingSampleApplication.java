package com.iamsumeet.metric.grouping.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootApplication
public class GroupingSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupingSampleApplication.class, args);
    }
}
