package com.esure.api.homepricingorchestration.configuration;

/**
 * POJO containing config for RabbitMQ Camel endpoint
 */
public class RabbitEndpoint
{

    private String exchangeName;

    private String queueName;

    private String routingKey;

    public String getExchangeName()
    {
        return exchangeName;
    }

    public void setExchangeName(final String exchangeName)
    {
        this.exchangeName = exchangeName;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(final String queueName)
    {
        this.queueName = queueName;
    }

    public String getRoutingKey()
    {
        return routingKey;
    }

    public void setRoutingKey(final String routingKey)
    {
        this.routingKey = routingKey;
    }

}
