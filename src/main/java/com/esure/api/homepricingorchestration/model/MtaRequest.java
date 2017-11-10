package com.esure.api.homepricingorchestration.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MtaRequest
{

	@JsonProperty
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
