package org.owasp.appsensor.event;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.owasp.appsensor.core.AppSensorClient;
import org.owasp.appsensor.core.Attack;
import org.owasp.appsensor.core.Event;
import org.owasp.appsensor.core.Response;
import org.owasp.appsensor.core.event.EventManager;

/**
 * This event manager should perform rest style requests since it functions
 * as the reference rest client.
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 * @author Raphaël Taban
 */
@Named
public class RestEventManager implements EventManager {

	@Inject
	private AppSensorClient appSensorClient;
	
	private WebTarget target;
	
	private String clientApplicationIdName;
	private String clientApplicationIdValue;
	
	public RestEventManager() { }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addEvent(Event event) {
		//make request
		target
			.path("api")
			.path("v1.0")
			.path("events")
			.request()
			.header(clientApplicationIdName, clientApplicationIdValue)
			.post(Entity.entity(event, MediaType.APPLICATION_JSON), Event.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAttack(Attack attack) {
		//make request
		target
			.path("api")
			.path("v1.0")
			.path("attacks")
			.request()
			.header(clientApplicationIdName, clientApplicationIdValue)
			.post(Entity.entity(attack, MediaType.APPLICATION_JSON), Attack.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Response> getResponses(String earliest) {
		GenericType<Collection<Response>> responseType = new GenericType<Collection<Response>>() {};
        
		Collection<Response> responses = 
		        target
				.path("api")
				.path("v1.0")
				.path("responses")
				.queryParam("earliest", earliest)
				.request()
				.header(clientApplicationIdName, clientApplicationIdValue)
				.get(responseType);
		
		//make request
		return responses;
	}
	
	@PostConstruct
	private void initializeData() {
		String url = appSensorClient.getConfiguration().getServerConnection().getUrl();
		target = ClientBuilder.newClient().target(url);
		
		clientApplicationIdName = appSensorClient.getConfiguration().getServerConnection().getClientApplicationIdentificationHeaderNameOrDefault();
		clientApplicationIdValue = appSensorClient.getConfiguration().getServerConnection().getClientApplicationIdentificationHeaderValue();
	}
	
	// this is primarily used for testing, but in the case where a single client needs to look like multiple clients, this is useful
	public void updateApplicationIdentificationHeaderValue(String value) {
		clientApplicationIdValue = value;		
	}

}
