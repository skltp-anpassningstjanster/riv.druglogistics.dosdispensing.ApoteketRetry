/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.adapterservices.monitoring;

import java.util.Date;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.soitoolkit.commons.mule.util.ThreadSafeSimpleDateFormat;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.ConfigurationType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@WebService(
		serviceName = "PingForConfigurationResponderService", 
		endpointInterface="se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface", 
		portName = "PingForConfigurationResponderPort", 
		targetNamespace = "urn:riv:itintegration:monitoring:PingForConfiguration:1:rivtabp21",
		wsdlLocation = "ServiceContracts_itintegration_monitoring/interactions/PingForConfigurationInteraction/PingForConfigurationInteraction_1.0_RIVTABP21.wsdl")
public class PingForconfigurationService implements PingForConfigurationResponderInterface{
	
	private ThreadSafeSimpleDateFormat dateFormat = new ThreadSafeSimpleDateFormat("yyyyMMddhhmmss");
	
	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("ApoteketRetryAdapter-config","ApoteketRetryAdapter-config-override");
	
	private static final Logger log = LoggerFactory.getLogger(PingForconfigurationService.class);

	@Override
	public PingForConfigurationResponseType pingForConfiguration(String logicalAddress,
			PingForConfigurationType parameters) {
		
		log.info("PingForConfiguration requested for {}", rb.getString("APPLICATION_NAME"));
		
		PingForConfigurationResponseType response = new PingForConfigurationResponseType();
		response.setPingDateTime(dateFormat.format(new Date()));
		response.getConfiguration().add(createConfigurationInfo("Applikation",rb.getString("APPLICATION_NAME")));
		
		log.info("PingForConfiguration response returned for {}", rb.getString("APPLICATION_NAME"));
		
		return response;
	}

	private ConfigurationType createConfigurationInfo(String name, String value) {
		log.debug("PingForConfiguration config added [{}: {}]", name, value);
		
		ConfigurationType configurationInfo = new ConfigurationType();
		configurationInfo.setName(name);
		configurationInfo.setValue(value);
		return configurationInfo;
	}

}
