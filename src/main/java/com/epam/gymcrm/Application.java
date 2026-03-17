package com.epam.gymcrm;

import com.epam.gymcrm.bootstrap.DemoRunner;
import com.epam.gymcrm.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            DemoRunner demoRunner = context.getBean(DemoRunner.class);
            demoRunner.runDemo();
        }
    }
}