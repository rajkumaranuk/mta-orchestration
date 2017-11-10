package com.esure.api.homepricingorchestration.processor;

import java.net.URI;
import java.net.URISyntaxException;

import com.esure.api.homepricingorchestration.configuration.RefDataConfiguration;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.esure.api.homepricingorchestration.configuration.IPTConfiguration;
import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Processor to make call to IPT Home Pricing Service
 */
@Component
public class CallRefDataServiceProcessor implements Processor
{

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private URI refDataServiceURI;

    private static final Logger LOGGER = LoggerFactory.getLogger(CallRefDataServiceProcessor.class);

    /**
     * Constructor to wire in dependencies and to construct the URI for calling the IPT Home Service
     *
     * @param refDataConfiguration                     Configuration for calling the IPT Home Service
     * @param restTemplate                         RestTemplate to use for calling the IPT Home Service
     * @param objectMapper                         ObjectMapper to convert to/from JSON and POJOs
     * @throws URISyntaxException if the URI constructed from the IPTHomeServiceCallerConfiguration is not a valid URI
     */
    @Autowired
    public CallRefDataServiceProcessor(
            RefDataConfiguration refDataConfiguration,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) throws URISyntaxException
    {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.refDataServiceURI = new URI(refDataConfiguration.getUrl());
    }

    /**
     * Make the request to the IPT home service.
     */
    @Override
    public void process(final Exchange exchange) throws Exception
    {
        try
        {
            final MtaInfo mtaInfo = objectMapper.readValue(exchange.getIn().getBody(byte[].class), MtaInfo.class);
            final String refDataResponse = restTemplate.getForEntity(refDataServiceURI, String.class).getBody();

            mtaInfo.setRefData(refDataResponse);
            exchange.getIn().setBody(objectMapper.writeValueAsBytes(mtaInfo));
        }

        catch (HttpStatusCodeException e)

        {
            e.printStackTrace();
            LOGGER.error("Exception in IPT Service Response : " + e.getResponseBodyAsString());
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, e.getStatusCode().value());
            exchange.getIn().setBody(e.getMessage().getBytes());
        }

    }
}
