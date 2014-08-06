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
package se.skltp.adapterservices.druglogistics.dosdispensing;

import java.text.MessageFormat;

import org.apache.commons.lang.StringEscapeUtils;
import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transformer to be used as a responseTransformer for a SOAP based service.
 * Creates a SOAP Fault structure with exception information as response if an error has occurred in the processing of the request.
 * 
 * @author Magnus Larsson
 *
 */
public class CreateSoapFaultIfExceptionTransformer extends AbstractMessageTransformer {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
    static String SOAP_FAULT_V11 = 
    	"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    	"  <soapenv:Header/>" + 
    	"  <soapenv:Body>" + 
		"    <soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
		"      <faultcode>soap:Server</faultcode>\n" + 
		"      <faultstring>{0}</faultstring>\n" +
		"      <faultactor>{1}</faultactor>\n" +
		"      <detail>\n" +
		"        {2}\n" +
		"      </detail>\n" + 
		"    </soap:Fault>" + 
		"  </soapenv:Body>" + 
		"</soapenv:Envelope>";
    
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
    	logger.debug("transform() called");
    		
		// Take care of any error message and send it back as a SOAP Fault!
		// Is there an exception-payload?
		ExceptionPayload ep = message.getExceptionPayload();
        if (ep == null) {
        	
        	// No, it's no, just bail out returning what we got
        	logger.debug("No error, return origin message");
        	return message;
        }

		logger.debug("ExceptionPayload detected as well, let's create a SOAP-FAULT!");
		EventLoggerWrapper eventLog = new EventLoggerWrapper(muleContext, message);

    	String soapFault = createSoapFaultFromExceptionPayload(eventLog, ep);
    	logger.debug("Created soapFault: {}", soapFault);

        // Now the exception payload is transformed to a SOAP-Fault, remove the ExceptionPayload!
		logger.debug("Set ExceptionPayload to null and outbound http.status=500");
        message.setExceptionPayload(null);
        message.setProperty("http.status", 500, PropertyScope.OUTBOUND);
        message.setPayload(soapFault);
        
        return message;
	        
	}

    protected String createSoapFaultFromExceptionPayload(EventLoggerWrapper eventLog, ExceptionPayload ep) {
    	
    	// Use the root exception if any otherwise the exception
		logger.debug("Exception: "     + ep.getException()     + ", " + ep.getException().getClass().getName());
		logger.debug("RootException: " + ep.getRootException() + ", " + ep.getRootException().getClass().getName());
    	Throwable e = (ep.getRootException() != null) ? ep.getRootException() : ep.getException();

//		FIXME: Can't get soap fault over to the client!!!
//		String errMsg   = ep.getCode() + ": " + ep.getMessage();
        String errMsg   = e.getMessage();
        String endpoint = getEndpointAddress();
        String detail   = e.getMessage();
        
		eventLog.logError(e, "Processing failed, return SOAP Fault");
        
        return createSoapFault(errMsg, endpoint, detail);
	}

	private String getEndpointAddress() {
		String ea = "UNKNOWN";
		if (getEndpoint() != null && getEndpoint().getEndpointURI() != null) {
			ea = getEndpoint().getEndpointURI().getAddress();
		}
		return ea;
	}

	protected String createSoapFault(String errMsg, String endpoint, String details) {
		return MessageFormat.format(SOAP_FAULT_V11, StringEscapeUtils.escapeXml(errMsg), endpoint, StringEscapeUtils.escapeXml(details));
	}

}			