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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;

public class PingForconfigurationServiceIntegrationTest extends FunctionalTestCase {

	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("ApoteketRetryAdapter-config",
			"ApoteketRetryAdapter-config-override");

	private String url = null;

	public PingForconfigurationServiceIntegrationTest() {
		url = rb.getString("PINGFORCONFIGURATION_INBOUND_ENDPOINT");
	}

	@Override
	protected String getConfigResources() {
		return "PingForConfiguration-rivtabp21-service.xml," + "ApoteketRetryAdapter-common.xml";
	}

	@Test
	public void pingForConfiguration() throws Exception {

		PingForConfigurationTestConsumer consumer = new PingForConfigurationTestConsumer(url);
		PingForConfigurationResponseType response = consumer.callService("logicalAddress");

		assertNotNull(response.getPingDateTime());
		assertEquals("Applikation", response.getConfiguration().get(0).getName());
		assertEquals(rb.getString("APPLICATION_NAME"), response.getConfiguration().get(0).getValue());
	}

}
