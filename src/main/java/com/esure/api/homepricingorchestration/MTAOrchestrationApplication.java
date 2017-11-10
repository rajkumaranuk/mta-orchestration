package com.esure.api.homepricingorchestration;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * The Main Application class
 * <p>
 * Entry point for the application
 * </p>
 */
@SpringBootApplication
public class MTAOrchestrationApplication
{

    /**
     * The main method for running the application.
     * <p>
     * Uses a {@code CamelSpringBootApplicationController} to block the main thread and keep the spring/camel context running.
     * </p>
     *
     * @param args command line args
     */
    public static void main(final String[] args)
    {
        final ApplicationContext applicationContext = new SpringApplication(MTAOrchestrationApplication.class).run(args);
        final CamelSpringBootApplicationController applicationController = applicationContext.getBean(CamelSpringBootApplicationController.class);
        applicationController.run();
    }

}
