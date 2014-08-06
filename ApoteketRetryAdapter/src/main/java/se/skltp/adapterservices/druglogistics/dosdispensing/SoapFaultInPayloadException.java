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

public class SoapFaultInPayloadException extends RuntimeException {

	public SoapFaultInPayloadException() {
		super();
	}

	public SoapFaultInPayloadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SoapFaultInPayloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoapFaultInPayloadException(String message) {
		super(message);
	}

	public SoapFaultInPayloadException(Throwable cause) {
		super(cause);
	}

	private String soapFault = null;
	public void setSoapFault(String soapFault) {
		this.soapFault = soapFault;
	}
	public String getSoapFault() {
		return soapFault;
	}
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -9093275941853317643L;

}
