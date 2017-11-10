package com.esure.api.homepricingorchestration.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Configuration class to hold general rabbitmq details
 */
@ConfigurationProperties("rabbitmq")
@Configuration @Validated
@RefreshScope
public class RabbitmqConfiguration
{

    @NotNull
    private String hostname;

    @NotNull
    private String port;

    @NotNull
    private String camelEndpointTemplate;

    @NotNull
    @NestedConfigurationProperty
    private RabbitEndpoint welcomeRabbitEndpoint;

    @NotNull
    @NestedConfigurationProperty
    private RabbitEndpoint iptRabbitEndpoint;

    @NotNull
    @NestedConfigurationProperty
    private RabbitEndpoint postcodeRabbitEndpoint;

    @NotNull
    @NestedConfigurationProperty
    private RabbitEndpoint refDataRabbitEndpoint;

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getCamelEndpointTemplate()
    {
        return camelEndpointTemplate;
    }

    public void setCamelEndpointTemplate(String camelEndpointTemplate)
    {
        this.camelEndpointTemplate = camelEndpointTemplate;
    }

    public RabbitEndpoint getIptRabbitEndpoint()
    {
        return iptRabbitEndpoint;
    }

    public void setIptRabbitEndpoint(RabbitEndpoint iptRabbitEndpoint)
    {
        this.iptRabbitEndpoint = iptRabbitEndpoint;
    }

    public RabbitEndpoint getPostcodeRabbitEndpoint()
    {
        return postcodeRabbitEndpoint;
    }

    public void setPostcodeRabbitEndpoint(RabbitEndpoint postcodeRabbitEndpoint)
    {
        this.postcodeRabbitEndpoint = postcodeRabbitEndpoint;
    }

    public RabbitEndpoint getWelcomeRabbitEndpoint()
    {
        return welcomeRabbitEndpoint;
    }

    public void setWelcomeRabbitEndpoint(RabbitEndpoint welcomeRabbitEndpoint)
    {
        this.welcomeRabbitEndpoint = welcomeRabbitEndpoint;
    }

    public RabbitEndpoint getRefDataRabbitEndpoint()
    {
        return refDataRabbitEndpoint;
    }

    public void setRefDataRabbitEndpoint(RabbitEndpoint refDataRabbitEndpoint)
    {
        this.refDataRabbitEndpoint = refDataRabbitEndpoint;
    }
}
