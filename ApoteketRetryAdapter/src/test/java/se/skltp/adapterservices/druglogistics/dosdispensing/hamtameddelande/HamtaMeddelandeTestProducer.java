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
package se.skltp.adapterservices.druglogistics.dosdispensing.hamtameddelande;

import java.util.GregorianCalendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.w3c.addressing_1_0.AttributedURIType;

import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenResponderInterface;
import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenResponseType;
import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenType;
import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandeninfoResponse;
import se.riv.druglogistics.dosedispensing_1.IdentitetstypEnum;
import se.riv.druglogistics.dosedispensing_1.MeddelandePrioritetEnum;
import se.riv.druglogistics.dosedispensing_1.MeddelandeStatusEnum;
import se.riv.druglogistics.dosedispensing_1.PatientinfoResponse;
import se.riv.druglogistics.dosedispensing_1.YrkesKodEnum;

@WebService(serviceName = "HamtaMeddelandenResponderService", endpointInterface = "se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenResponderInterface", portName = "HamtaMeddelandenResponderPort", targetNamespace = "urn:riv:druglogistics:dosedispensing:HamtaMeddelanden:1:rivtabp20")

public class HamtaMeddelandeTestProducer implements HamtaMeddelandenResponderInterface {
	
	private static final Logger log = LoggerFactory.getLogger(HamtaMeddelandeTestProducer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("ApoteketRetryAdapter-config-override", "ApoteketRetryAdapter-config");
	private static final long SERVICE_TIMEOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));

	//OK responses
	public static final String ONE_OK_RESPONSE = "1111111111";
	public static final String HUNDRED_MESSAGES_OK_RESPONSE = "5555555555";
	
	//Exception responses
	public static final String TIMEOUT_RESPONSE = "9999999990";
	public static final String TWO_ERROR_RESPONSE = "9999999991";
	public static final String ALWAYS_ERROR_RESPONSE = "9999999999";
			
	private static int numberOfPermanentErrors = 0;
	private static int numberOfErrors = 0;

	@Override
	@WebResult(name = "HamtaMeddelandenResponse", targetNamespace = "urn:riv:druglogistics:dosedispensing:HamtaMeddelandenResponder:1", partName = "parameters")
	@WebMethod(operationName = "HamtaMeddelanden", action = "urn:riv:druglogistics:dosedispensing:HamtaMeddelandenResponder:1:HamtaMeddelanden")
	public HamtaMeddelandenResponseType hamtaMeddelanden(
			@WebParam(partName = "LogicalAddress", name = "To", targetNamespace = "http://www.w3.org/2005/08/addressing", header = true) AttributedURIType arg0,
			@WebParam(partName = "parameters", name = "HamtaMeddelanden", targetNamespace = "urn:riv:druglogistics:dosedispensing:HamtaMeddelandenResponder:1") HamtaMeddelandenType request) {

		
		String glnKod = request.getGlnkod();
		
		HamtaMeddelandenResponseType responseType = new HamtaMeddelandenResponseType();
		responseType.setMeddelandeid((int) Math.random());
		responseType.setMeddelandetext("Meddelande text");
		responseType.setResultatkod("Resultatkod");

		if (HUNDRED_MESSAGES_OK_RESPONSE.equals(glnKod)) {
			int numberOfResponses = 100;
			log.info("Create OK response with " + numberOfResponses + " meddelanden.");
			createResponse(responseType, glnKod, numberOfResponses);

		} else if (ONE_OK_RESPONSE.equals(glnKod)) {
			int numberOfResponses = 1;
			log.info("Create OK response with " + numberOfResponses + " meddelanden.");
			createResponse(responseType, glnKod, numberOfResponses);
		
		} else if (TWO_ERROR_RESPONSE.equals(glnKod)) {
			
			/*
			 * Need to simulate that producer always return error the 2 first times and that
			 * the third time the response is ok. This is needed to test retry handling. 
			 */
			if(numberOfErrors > 1){
				log.info("Create OK response after " + numberOfErrors + " of failures.");
				responseType.getMeddelanden().add(createMeddelandeResponse(glnKod));
				numberOfErrors = 0;
			}else{
				numberOfErrors++;
				log.info("Create Error #" + numberOfErrors);
				throw new RuntimeException("Error occured when trying to retrive information from using glnkod: " + glnKod);
			}
		} else if(ALWAYS_ERROR_RESPONSE.equals(glnKod)){
			log.info("Create Permanent Error ({} errors)", ++numberOfPermanentErrors);
			throw new RuntimeException("Error occured when trying to retrive information from using glnkod: " + glnKod);
		}else if (TIMEOUT_RESPONSE.equals(glnKod)) {
			log.info("Create Timeout Error");
			timeOutResponse(responseType, glnKod);
		} 

		return responseType;
	}

	private void timeOutResponse(HamtaMeddelandenResponseType responseType, String glnKod) {
		try {
			long millis = SERVICE_TIMEOUT_MS + 30000;
			log.info("ZZZleeeping for " + millis + " ms...");
			Thread.sleep(millis);
			log.info("ZZZleeeping done");
		} catch (InterruptedException e) {
		}
		responseType.getMeddelanden().add(createMeddelandeResponse(glnKod));
	}

	private void createResponse(HamtaMeddelandenResponseType responseType, String glnKod, int numberOfResponses) {
		for (int i = 0; i < numberOfResponses; i++) {
			responseType.getMeddelanden().add(createMeddelandeResponse(glnKod));
		}
	}

	private HamtaMeddelandeninfoResponse createMeddelandeResponse(String glnkod) {
		HamtaMeddelandeninfoResponse response = new HamtaMeddelandeninfoResponse();
		response.setDosapoteknamn("DOS-APOTEKETES NAMN");
		response.setGlnkod(glnkod);
		response.setKommunikationsriktning(0);
		response.setMeddelande("Meddelande text");
		response.setMeddelandeid("Meddelandeid");
		response.setMeddelandestatus(MeddelandeStatusEnum.O);
		response.setMeddelandestatusarbetsplats("Meddelandestatus arbetsplats");
		response.setMeddelandestatusefternamn("Meddelandestatus efternamn");
		response.setMeddelandestatusfornamn("Meddelandestatus fornamn");
		response.setOrdinationsid("Ordinationsid");
		response.setPatientinformation(createPatientResponse());
		response.setPrioritet(MeddelandePrioritetEnum.H);
		response.setRubrik("Rubrik");
		response.setSandarearbetsplats("Sandare arbetsplats");
		response.setSandareefternamn("Sandare efternamn");
		response.setSandarefornamn("Sandare fornamn");
		response.setSandarehsaid("Sandare hsaid");
		response.setSandareyrkeskod(YrkesKodEnum.LK);
		response.setSandningstidpunkt(createNow());
		response.setStatustidpunkt(createNow());
		return response;
	}

	private XMLGregorianCalendar createNow() {
		try {
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
			XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
			return now;
		} catch (Exception e) {
			return null;
		}
	}

	private PatientinfoResponse createPatientResponse() {
		PatientinfoResponse response = new PatientinfoResponse();
		response.setEfternamn("Tolvansson");
		response.setFornamn("Tolvan");
		response.setIdentitetstyp(IdentitetstypEnum.P);
		response.setKommunkod("Kommunkod");
		response.setLanskod("Landskod");
		response.setMellannamn("Tolv");
		response.setPersonid("191212121212");
		return response;
	}
}


