package com.esure.api.homepricingorchestration.aggregator;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EnrichAggregationStrategy implements AggregationStrategy
{

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange)
	{
		Object object = newExchange.getIn().getBody();

//		if (oldExchange.getPattern().isOutCapable()) {
//			oldExchange.getOut().setBody(object);
//		} else {
			oldExchange.getIn().setBody(object);
//		}
		return oldExchange;
	}
}
