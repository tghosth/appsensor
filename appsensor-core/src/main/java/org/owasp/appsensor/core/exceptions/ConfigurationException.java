package org.owasp.appsensor.core.exceptions;

/**
 * This exception is for anytime the configuration for appsensor is invalid.
 * 
 * This is used by the {@link org.owasp.appsensor.core.configuration.client.ClientConfigurationReader}
 * and the {@link org.owasp.appsensor.core.configuration.server.ServerConfigurationReader}
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
public class ConfigurationException extends Exception  {

	private static final long serialVersionUID = 538520201225584981L;

	public ConfigurationException(String s) {
		super(s);
	}
	
	public ConfigurationException(String s, Throwable cause) {
		super(s, cause);
	}
	
}
