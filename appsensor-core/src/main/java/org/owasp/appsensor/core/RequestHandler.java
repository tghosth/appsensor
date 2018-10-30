package org.owasp.appsensor.core;

import java.util.Collection;

import org.owasp.appsensor.core.exceptions.NotAuthorizedException;

/**
 * The RequestHandler is the key interface that the server side of 
 * AppSensor implements to handle the different functional requests.
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
public interface RequestHandler {
	
	public static String APPSENSOR_CLIENT_APPLICATION_IDENTIFIER_ATTR = "APPSENSOR_CLIENT_APPLICATION_IDENTIFIER_ATTR";
	
	/**
	 * Add an Event.
	 * 
	 * @param event Event to add
	 */
	public void addEvent(Event event) throws NotAuthorizedException;
	
	/**
	 * Add an Attack
	 * @param attack Attack to add
	 */
	public void addAttack(Attack attack) throws NotAuthorizedException;
	
	/**
	 * Retrieve any responses generated that apply to this client application 
	 * since the last time the client application called this method.
	 *  
	 * @param earliest Timestamp in the http://tools.ietf.org/html/rfc3339 format
	 * @return a Collection of Response objects 
	 */
	public Collection<Response> getResponses(String earliest) throws NotAuthorizedException;
	
}
