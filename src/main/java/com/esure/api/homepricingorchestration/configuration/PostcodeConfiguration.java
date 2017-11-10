package com.esure.api.homepricingorchestration.configuration;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class for Postcode properties.
 */
@ConfigurationProperties("postcode")
@Configuration
@Validated
@RefreshScope
public class PostcodeConfiguration
{

    @NotNull
    private String url;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
