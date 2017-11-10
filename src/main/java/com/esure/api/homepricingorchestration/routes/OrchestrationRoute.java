package com.esure.api.homepricingorchestration.routes;

import java.util.concurrent.Executors;

import com.esure.api.homepricingorchestration.aggregator.EnrichAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.esure.api.homepricingorchestration.aggregator.MulticastAggregationStrategy;
import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.esure.api.homepricingorchestration.model.MtaRequest;
import com.esure.api.homepricingorchestration.processor.CallIPTServiceProcessor;
import com.esure.api.homepricingorchestration.processor.CallPostcodeServiceProcessor;
import com.esure.api.homepricingorchestration.processor.CallRefDataServiceProcessor;
import com.esure.api.homepricingorchestration.utils.MtaInfoConvertor;
import com.esure.api.homepricingorchestration.utils.MtaInfoGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The main orchestration route.
 */
@Component
public class OrchestrationRoute extends RouteBuilder
{
    @Autowired private ObjectMapper objectMapper;

    @Autowired @Qualifier("welcomeEndpoint") private String welcomeEndpoint;
    @Autowired @Qualifier("iptEndpoint") private String iptEndpoint;
    @Autowired @Qualifier("postcodeEndpoint") private String postcodeEndpoint;
    @Autowired @Qualifier("refDataEndpoint") private String refDataEndpoint;

    @Autowired private MulticastAggregationStrategy mtaInfoAggregationStrategy;
    @Autowired private EnrichAggregationStrategy enrichAggregationStrategy;

    @Autowired private CallPostcodeServiceProcessor callPostcodeServiceProcessor;
    @Autowired private CallIPTServiceProcessor callIPTServiceProcessor;
    @Autowired private CallRefDataServiceProcessor callRefDataServiceProcessor;

    @Override public void configure() throws Exception
    {
        rest("/mta")
                .post()
                .type(MtaRequest.class)
                .route()
                .bean(MtaInfoGenerator.class)
                .process(exchange -> exchange.getIn().setBody(objectMapper.writeValueAsBytes(exchange.getIn().getBody(MtaInfo.class))))
                .to(welcomeEndpoint)
                .process(exchange -> exchange.getOut().setBody(objectMapper.readValue(exchange.getIn().getBody(byte[].class), MtaInfo.class)))
                .bean(MtaInfoConvertor.class)
                .endRest();

        from(welcomeEndpoint)
                .process(exchange -> exchange.setProperty(RabbitMQConstants.CORRELATIONID, exchange.getIn().getHeader(RabbitMQConstants.CORRELATIONID)))
                .removeHeaders("rabbitmq.*")
                .multicast(mtaInfoAggregationStrategy)
                .parallelProcessing()
                .stopOnException()
                .shareUnitOfWork()
                .executorService(Executors.newFixedThreadPool(12))
                .to(iptEndpoint, postcodeEndpoint)
                .end()
                .process(exchange -> exchange.getIn().setBody(objectMapper.writeValueAsBytes(exchange.getIn().getBody(MtaInfo.class))))
                .enrich(refDataEndpoint, enrichAggregationStrategy)
                .process(exchange -> exchange.getIn().setHeader(RabbitMQConstants.CORRELATIONID, exchange.getProperty(RabbitMQConstants.CORRELATIONID)));

        from(iptEndpoint).process(callIPTServiceProcessor);

        from(postcodeEndpoint).process(callPostcodeServiceProcessor);

        from(refDataEndpoint).process(callRefDataServiceProcessor);
    }
}





