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
