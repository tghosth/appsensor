package org.owasp.appsensor.local.handler;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.owasp.appsensor.core.AppSensorServer;
import org.owasp.appsensor.core.Attack;
import org.owasp.appsensor.core.Event;
import org.owasp.appsensor.core.RequestHandler;
import org.owasp.appsensor.core.Response;
import org.owasp.appsensor.core.criteria.SearchCriteria;
import org.owasp.appsensor.core.exceptions.NotAuthorizedException;
import org.owasp.appsensor.core.logging.Loggable;
import org.owasp.appsensor.core.util.StringUtils;
import org.slf4j.Logger;

/**
 * This is the local endpoint that handles requests on the server-side.
 * 
 * Since this is a local implementation, there is no need for access control.
 * There are no requests coming from anywhere other than self, so it's trusted. 
 * 
 * Additionally, client/server is actually just an API call in the same JVM instance, 
 * but is separated to maintain the architectural design. Simple delegation 
 * lets us use the same pattern here. 
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 * @author Raphaël Taban
 */
@Named
@Loggable
public class LocalRequestHandler implements RequestHandler {

	@SuppressWarnings("unused")
	private Logger logger;
	
	@Inject
	private AppSensorServer appSensorServer;
	
	private static String detectionSystemId = null;	//start with blank
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addEvent(Event event) throws NotAuthorizedException {
		if (detectionSystemId == null) {
			detectionSystemId = event.getDetectionSystem().getDetectionSystemId();
		}
		
		appSensorServer.getEventStore().addEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAttack(Attack attack) throws NotAuthorizedException {
		if (detectionSystemId == null) {
			detectionSystemId = attack.getDetectionSystem().getDetectionSystemId();
		}
		
		appSensorServer.getAttackStore().addAttack(attack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Response> getResponses(String earliest) throws NotAuthorizedException {
		SearchCriteria criteria = new SearchCriteria().
				setDetectionSystemIds(StringUtils.toCollection(detectionSystemId != null ? detectionSystemId : "")).
				setEarliest(earliest);
		
		return appSensorServer.getResponseStore().findResponses(criteria);
	}

}