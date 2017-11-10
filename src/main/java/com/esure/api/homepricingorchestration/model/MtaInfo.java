package com.esure.api.homepricingorchestration.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MtaInfo
{

	private String name;

	private Double tax;

	private String postcode;

	private String refData;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Double getTax()
	{
		return tax;
	}

	public void setTax(Double tax)
	{
		this.tax = tax;
	}

	public String getPostcode()
	{
		return postcode;
	}

	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
	}

	public String getRefData()
	{
		return refData;
	}

	public void setRefData(String refData)
	{
		this.refData = refData;
	}
}
