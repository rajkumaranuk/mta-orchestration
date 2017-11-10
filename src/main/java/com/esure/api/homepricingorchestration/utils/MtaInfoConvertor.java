package com.esure.api.homepricingorchestration.utils;

import org.apache.camel.Body;

import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.esure.api.homepricingorchestration.model.MtaResponse;

public class MtaInfoConvertor
{
	public MtaResponse convert(@Body MtaInfo mtaInfo) {
		MtaResponse mtaResponse = new MtaResponse();
		mtaResponse.setName(mtaInfo.getName());
		mtaResponse.setTax(mtaInfo.getTax());
		mtaResponse.setPostcode(mtaInfo.getPostcode());
		mtaResponse.setRefData(mtaInfo.getRefData());
		return mtaResponse;
	}
}
