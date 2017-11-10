package com.esure.api.homepricingorchestration.processor;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.esure.api.homepricingorchestration.configuration.PostcodeConfiguration;
import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Processor to make call to Postcode Home Pricing Service
 */
@Component
public class CallPostcodeServiceProcessor implements Processor
{

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private URI postcodeServiceURI;

    private static final Logger LOGGER = LoggerFactory.getLogger(CallPostcodeServiceProcessor.class);

    /**
     * Constructor to wire in dependencies and to construct the URI for calling the Postcode Home Service
     *
     * @param postcodeConfiguration                     Configuration for calling the Postcode Home Service
     * @param restTemplate                         RestTemplate to use for calling the Postcode Home Service
     * @param objectMapper                         ObjectMapper to convert to/from JSON and POJOs
     * @throws URISyntaxException if the URI constructed from the PostcodeHomeServiceCallerConfiguration is not a valid URI
     */
    @Autowired
    public CallPostcodeServiceProcessor(
            PostcodeConfiguration postcodeConfiguration,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) throws URISyntaxException
    {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.postcodeServiceURI = new URI(postcodeConfiguration.getUrl());
    }

    /**
     * Make the request to the Postcode home service.
     */
    @Override
    public void process(final Exchange exchange) throws Exception
    {
        try
        {
            final MtaInfo mtaInfo = objectMapper.readValue(exchange.getIn().getBody(byte[].class), MtaInfo.class);
            final String postcodeResponse = restTemplate.getForEntity(postcodeServiceURI, String.class).getBody();

            mtaInfo.setPostcode(postcodeResponse);
            exchange.getIn().setBody(objectMapper.writeValueAsBytes(mtaInfo));
        }

        catch (HttpStatusCodeException e)

        {
            e.printStackTrace();
            LOGGER.error("Exception in Postcode Service Response : " + e.getResponseBodyAsString());
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, e.getStatusCode().value());
            exchange.getIn().setBody(e.getMessage().getBytes());
        }

    }
}
