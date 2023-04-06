package it.eng.idsa.multipart.util;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil {
	
	private static DatatypeFactory datatypeFactory = null;
	
	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("Error while trying to obtain a new instance of DatatypeFactory", e);
		} 
	}
	
	public static XMLGregorianCalendar now() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis((new Date()).getTime());
		return datatypeFactory.newXMLGregorianCalendar(gc);
	}
	
	/**
	 * Use this method to get a proper dateTime. We need to have Z at the end instead of e.g. "+02:00", or we will get an exception during serialization.
	 * 
	 * @return Time in format: 2023-03-31T07:38:35.368Z
	 */
	public static XMLGregorianCalendar normalizedDateTime() {
		return now().normalize();
	}
	
}
