package com.esure.api.homepricingorchestration.aggregator;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MulticastAggregationStrategy implements AggregationStrategy
{

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange)
	{
		try
		{
			if (newExchange.getException() != null)
			{
				oldExchange.setException(newExchange.getException());
			}

			if (oldExchange == null)
			{
				return newExchange;
			}


			MtaInfo info = objectMapper.readValue(oldExchange.getIn().getBody(byte[].class), MtaInfo.class);
			MtaInfo recd = objectMapper.readValue(newExchange.getIn().getBody(byte[].class), MtaInfo.class);

			if (recd.getTax() != null) {
				info.setTax(recd.getTax());
			}

			if (recd.getPostcode() != null) {
				info.setPostcode(recd.getPostcode());
			}

			if (oldExchange.getPattern().isOutCapable()) {
				oldExchange.getOut().setBody(info);
			}

			return oldExchange;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
