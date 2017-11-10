package com.esure.api.homepricingorchestration.processor;

import com.esure.api.homepricingorchestration.configuration.IPTConfiguration;
import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.esure.api.homepricingorchestration.model.MtaRequest;
import com.esure.api.homepricingorchestration.model.MtaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Processor to make call to IPT Home Pricing Service
 */
@Component
public class CallIPTServiceProcessor implements Processor
{

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private URI iptServiceURI;

    private static final Logger LOGGER = LoggerFactory.getLogger(CallIPTServiceProcessor.class);

    /**
     * Constructor to wire in dependencies and to construct the URI for calling the IPT Home Service
     *
     * @param iptConfiguration                     Configuration for calling the IPT Home Service
     * @param restTemplate                         RestTemplate to use for calling the IPT Home Service
     * @param objectMapper                         ObjectMapper to convert to/from JSON and POJOs
     * @throws URISyntaxException if the URI constructed from the IPTHomeServiceCallerConfiguration is not a valid URI
     */
    @Autowired
    public CallIPTServiceProcessor(
            IPTConfiguration iptConfiguration,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) throws URISyntaxException
    {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.iptServiceURI = new URI(iptConfiguration.getUrl());
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
            final Double iptResponse = restTemplate.getForEntity(iptServiceURI, Double.class).getBody();

            mtaInfo.setTax(iptResponse);
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
