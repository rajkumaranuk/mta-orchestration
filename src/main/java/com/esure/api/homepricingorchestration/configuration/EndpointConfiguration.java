package com.esure.api.homepricingorchestration.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

/**
 * Configuration class for defining endpoint addresses.
 */
@Configuration
public class EndpointConfiguration
{

    private final RabbitmqConfiguration rabbitmqConfiguration;

    private String hostname;

    private String port;

    private String endpointTemplate;

    /**
     * Constructor to wire in dependencies and to set up constants
     *
     * @param rabbitmqConfiguration           Configuration specific to rabbitMQ
     */
    @Autowired
    public EndpointConfiguration(RabbitmqConfiguration rabbitmqConfiguration)
    {
        this.rabbitmqConfiguration = rabbitmqConfiguration;

        // Setup the constants
        hostname = rabbitmqConfiguration.getHostname();
        port = rabbitmqConfiguration.getPort();
        endpointTemplate = rabbitmqConfiguration.getCamelEndpointTemplate();
    }

    /**
     * Define the Welcome request queue.
     *
     * @return The IPT request queue to use in the camel route.
     */
    @Bean @Qualifier("welcomeEndpoint")
    public String welcomeEndpoint()
    {
        return constructRabbitEndpointString(rabbitmqConfiguration.getWelcomeRabbitEndpoint());
    }

    /**
     * Define the IPT request queue.
     *
     * @return The IPT request queue to use in the camel route.
     */
    @Bean @Qualifier("iptEndpoint")
    public String iptEndpoint()
    {
        return constructRabbitEndpointString(rabbitmqConfiguration.getIptRabbitEndpoint());
    }

    /**
     * Define the Postcode request queue.
     *
     * @return The IPT request queue to use in the camel route.
     */
    @Bean @Qualifier("postcodeEndpoint")
    public String postcodeEndpoint()
    {
        return constructRabbitEndpointString(rabbitmqConfiguration.getPostcodeRabbitEndpoint());
    }

    /**
     * Define the Postcode request queue.
     *
     * @return The IPT request queue to use in the camel route.
     */
    @Bean @Qualifier("refDataEndpoint")
    public String refDataEndpoint()
    {
        return constructRabbitEndpointString(rabbitmqConfiguration.getRefDataRabbitEndpoint());
    }

    /**
     * Generate the Rabbit Endpoint using the template and the params provided
     *
     * @param rabbitEndpoint the details to use to construct rabbitEndpoint to use with camel
     * @return the rabbit endpoint URI to use in a camel route
     */
    private String constructRabbitEndpointString(final RabbitEndpoint rabbitEndpoint)
    {
        return MessageFormat.format(endpointTemplate, hostname, port, rabbitEndpoint.getExchangeName(),
                rabbitEndpoint.getQueueName(), rabbitEndpoint.getRoutingKey());
    }

}
