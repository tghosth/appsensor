package org.owasp.appsensor.event.impl;

import java.util.Collection;

import org.owasp.appsensor.Attack;
import org.owasp.appsensor.Event;
import org.owasp.appsensor.EventManager;
import org.owasp.appsensor.Response;

/**
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 *
 */
public class SoapEventManager implements EventManager {

	//TODO: do a soap request based on configuration 
	
	@Override
	public void addEvent(Event event) {
		//make request
	}
	
	public void addAttack(Attack attack) {
		//make request
	}
	
	@Override
	public Collection<Response> getResponses() {
		//make request
		return null;
	}

}
