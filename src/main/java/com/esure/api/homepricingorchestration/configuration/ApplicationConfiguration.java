package com.esure.api.homepricingorchestration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * General Application Configuration
 */
@Configuration
public class ApplicationConfiguration
{

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Force use of Tomcat during tests. Wiremock adds Jetty to the classpath, which means the app runs on Jetty.
     * This negates that.
     *
     * @return embedded Tomcat
     */
    @Bean
    public TomcatEmbeddedServletContainerFactory tomcat()
    {
        return new TomcatEmbeddedServletContainerFactory();
    }

    /**
     * Define a {@code RestTemplate} bean that can be used throughout application.
     *
     * @return the {@link RestTemplate} object to use
     */
    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

    /**
     * Customise the Jackson Object Mapper to use the date format used by the {@code Calendar} class.
     * This customises the ObjectMapper provided and used by Spring.
     *
     * @return the {@link Jackson2ObjectMapperBuilderCustomizer} object to use to customise the Object Mapper.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer()
    {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.dateFormat(new SimpleDateFormat(DATE_FORMAT));
    }

    /**
     * Required for Camel to use ObjectMapper provided by Spring with the correct Java 8 date types and the above customisation
     *
     * @param objectMapper Autowired ObjectMapper provided by Spring
     * @return A bean with the name that Camel will resolve with custom mapper
     */
    @Bean(name = "json-jackson") @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public JacksonDataFormat jacksonDataFormat(@Autowired final ObjectMapper objectMapper)
    {
        //        objectMapper.registerModule(new JSR310Module());
        //        objectMapper.findAndRegisterModules();
        return new JacksonDataFormat(objectMapper, HashMap.class);
    }

    /**
     * Register the servlet so that Camel REST routes use the embedded Tomcat
     *
     * @return The bean used to register the servlet
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean()
    {
        final ServletRegistrationBean servlet = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
        servlet.setName("CamelServlet");
        return servlet;
    }

    @Bean
    public CamelContextConfiguration contextConfiguration()
    {
        return new CamelContextConfiguration()
        {
            @Override public void beforeApplicationStart(CamelContext context)
            {
                // your custom configuration goes here
                context.setHandleFault(true);
            }

            @Override public void afterApplicationStart(CamelContext camelContext)
            {
                camelContext.setHandleFault(true);

            }
        };
    }
}

