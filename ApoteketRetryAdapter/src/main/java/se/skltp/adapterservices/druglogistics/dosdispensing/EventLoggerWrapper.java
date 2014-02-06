package se.skltp.adapterservices.druglogistics.dosdispensing;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.soitoolkit.commons.logentry.schema.v1.LogLevelType;
import org.soitoolkit.commons.mule.api.log.EventLogMessage;

/**
 * Wrapper class for event-logger methods that hide Mule objects such as muleContext and muleMessage
 */
public class EventLoggerWrapper {
	private final CustomEventLogger eventLogger;
	private final MuleMessage muleMessage;

	public EventLoggerWrapper(MuleContext muleContext, MuleMessage muleMessage) {
		eventLogger = new CustomEventLogger();
		eventLogger.setMuleContext(muleContext);
		this.muleMessage = muleMessage;
	}

	public void logWarning(Throwable e, String logMessage) {
		log(e, logMessage, LogLevelType.WARNING);                
	}
	public void logError(Throwable e, String logMessage) {
		log(e, logMessage, LogLevelType.ERROR);                
	}

	private void log(Throwable e, String logMessage, LogLevelType logLevel) {
		EventLogMessage elm = new EventLogMessage();
		elm.setMuleMessage(muleMessage);
		elm.setLogMessage(logMessage);
		eventLogger.logErrorEvent(logLevel, e, elm);
	}
}
