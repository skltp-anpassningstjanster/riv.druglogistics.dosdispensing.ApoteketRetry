package se.skltp.adapterservices.druglogistics.dosdispensing.hamtameddelande;

import static se.skltp.adapterservices.druglogistics.dosdispensing.ApoteketRetryAdapterMuleServer.getAddress;

import java.net.URL;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.w3c.addressing_1_0.AttributedURIType;

import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenResponderInterface;
import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenResponseType;
import se.riv.druglogistics.dosedispensing_1.HamtaMeddelandenType;

public class HamtaMeddelandeTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(HamtaMeddelandeTestConsumer.class);

	@SuppressWarnings("unused")
	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("ApoteketRetryAdapter-config");

	private HamtaMeddelandenResponderInterface _service = null;
	    
    public HamtaMeddelandeTestConsumer(String serviceAddress) {
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(HamtaMeddelandenResponderInterface.class);
		proxyFactory.setAddress(serviceAddress);
		
		//Used for HTTPS
		SpringBusFactory bf = new SpringBusFactory();
		URL cxfConfig = HamtaMeddelandeTestConsumer.class.getClassLoader().getResource("cxf-test-consumer-config.xml");
		if (cxfConfig != null) {
			proxyFactory.setBus(bf.createBus(cxfConfig));
		}
		
		_service  = (HamtaMeddelandenResponderInterface) proxyFactory.create(); 
    }

    public static void main(String[] args) throws Exception {
            String serviceAddress = getAddress("HAMTAMEDDELANDE_INBOUND_URL");
            String glnkod = "1234567890";

            HamtaMeddelandeTestConsumer consumer = new HamtaMeddelandeTestConsumer(serviceAddress);
            HamtaMeddelandenResponseType response = consumer.callService(glnkod);
            log.info("Returned value = " + response.getResultatkod());
    }

    public HamtaMeddelandenResponseType callService(String id) throws Exception {
            log.debug("Calling sample-soap-service with id = {}", id);
            HamtaMeddelandenType request = new HamtaMeddelandenType();
            request.setGlnkod(id);
            return _service.hamtaMeddelanden(new AttributedURIType(), request);
    }	
	
}