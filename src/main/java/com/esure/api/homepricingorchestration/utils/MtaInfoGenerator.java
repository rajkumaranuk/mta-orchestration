package com.esure.api.homepricingorchestration.utils;

import org.apache.camel.Body;

import com.esure.api.homepricingorchestration.model.MtaInfo;
import com.esure.api.homepricingorchestration.model.MtaRequest;

public class MtaInfoGenerator
{
	public MtaInfo generate(@Body MtaRequest mtaRequest) {
		final MtaInfo mtaInfo = new MtaInfo();
		mtaInfo.setName(mtaRequest.getName());
		return mtaInfo;
	}
}
