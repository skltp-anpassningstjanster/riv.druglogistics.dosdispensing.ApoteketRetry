package se.skltp.adapterservices.druglogistics.dosdispensing.hamtameddelande;

import static org.junit.Assert.*;
import static se.skltp.adapterservices.druglogistics.dosdispensing.ApoteketRetryAdapterMuleServer.getAddress;
import static se.skltp.adapterservices.druglogistics.dosdispensing.hamtameddelande.HamtaMeddelandeTestProducer.*;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.AbstractJmsTestUtil;
import org.soitoolkit.commons.mule.test.ActiveMqJmsTestUtil;
 
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;

import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenResponseType;

 
public class HamtaMeddelandeIntegrationTest extends AbstractTestCase {
 
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(HamtaMeddelandeIntegrationTest.class);
	
 
	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
 

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("HAMTAMEDDELANDE_INBOUND_URL");
 
 
	private static final String ERROR_LOG_QUEUE = "SOITOOLKIT.LOG.ERROR";
	private AbstractJmsTestUtil jmsUtil = null;
 

    public HamtaMeddelandeIntegrationTest() {
    
 
        // Only start up Mule once to make the tests run faster...
        // Set to false if tests interfere with each other when Mule is started only once.
        setDisposeContextPerClass(true);
    }

	protected String getConfigResources() {
		return	"soitoolkit-mule-jms-connector-activemq-embedded.xml," + 
				"soitoolkit-mule-https-connector.xml," + 
				"ApoteketRetryAdapter-common.xml," +
				"hamtaMeddelande-service.xml," +
				"teststub-services/hamtaMeddelande-teststub-service.xml";
    }

    @Override
	protected void doSetUp() throws Exception {
		super.doSetUp();

		doSetUpJms();
  
     }

	private void doSetUpJms() {
		// TODO: Fix lazy init of JMS connection et al so that we can create jmsutil in the declaration
		// (The embedded ActiveMQ queue manager is not yet started by Mule when jmsutil is delcared...)
		if (jmsUtil == null) jmsUtil = new ActiveMqJmsTestUtil();
		
 
		// Clear queues used for error handling
		jmsUtil.clearQueues(ERROR_LOG_QUEUE);
    }


    @Test
    public void test_ok() throws Exception {
    	String id = ONE_OK_RESPONSE;
    	HamtaMeddelandeTestConsumer consumer = new HamtaMeddelandeTestConsumer(DEFAULT_SERVICE_ADDRESS);
		HamtaMeddelandenResponseType response = consumer.callService(id);
		assertEquals(id,  response.getMeddelanden().get(0).getGlnkod());
	}

    /**
     * @throws Exception
     */
    @Test
	public void test_fault_always_error() throws Exception {
    	String id = ALWAYS_ERROR_RESPONSE;
		try {
	    	HamtaMeddelandeTestConsumer consumer = new HamtaMeddelandeTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(id);
	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));
	    } catch (SOAPFaultException e) {
	    	assertEquals("Error occured when trying to retrive information from using glnkod: " + id, e.getMessage());
	    }
	}
    
    @Test
	public void test_retry_handling_ok_after_two_retries() throws Exception {
    	String id = TWO_ERROR_RESPONSE;
    	HamtaMeddelandeTestConsumer consumer = new HamtaMeddelandeTestConsumer(DEFAULT_SERVICE_ADDRESS);
		HamtaMeddelandenResponseType response = consumer.callService(id);
		assertEquals(id,  response.getMeddelanden().get(0).getGlnkod());
	}

    /**
     * @throws Exception
     */
    @Test
	public void test_fault_timeout() throws Exception {
        try {
	    	String id = TIMEOUT_RESPONSE;
	    	HamtaMeddelandeTestConsumer consumer = new HamtaMeddelandeTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(id);
	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));
        } catch (SOAPFaultException e) {
	    	assertEquals("Read timed out", e.getMessage());
        }

		// Sleep for a short time period  to allow the JMS response message to be delivered, otherwise ActiveMQ data store seems to be corrupt afterwards...
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
    }
 

}
