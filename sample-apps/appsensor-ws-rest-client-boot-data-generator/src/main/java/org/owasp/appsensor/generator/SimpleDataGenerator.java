package org.owasp.appsensor.generator;

import java.util.Random;

import javax.inject.Named;

import org.owasp.appsensor.AppsensorWsRestClientBootDataGeneratorApplication;
import org.owasp.appsensor.core.DetectionPoint;
import org.owasp.appsensor.core.DetectionSystem;
import org.owasp.appsensor.core.Event;
import org.owasp.appsensor.core.IPAddress;
import org.owasp.appsensor.core.User;
import org.owasp.appsensor.core.geolocation.GeoLocation;
import org.owasp.appsensor.event.RestEventManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

@Named
public class SimpleDataGenerator {

	private Gson gson = new Gson();

	private User bob = new User("bob", new IPAddress("10.10.10.1", new GeoLocation(37.596758, -121.647992)));
	
	// 5 in 20 seconds (1 every 4 seconds is an attack)
	private DetectionPoint ie1 = new DetectionPoint(DetectionPoint.Category.INPUT_VALIDATION, "IE1");
	// 6 in 30 seconds (1 every 5 seconds is an attack)
	private DetectionPoint ie2 = new DetectionPoint(DetectionPoint.Category.INPUT_VALIDATION, "IE2");
	// 7 in 40 seconds (1 every 5.7 seconds is an attack)
	private DetectionPoint re3 = new DetectionPoint(DetectionPoint.Category.REQUEST, "RE3");
	// 8 in 50 seconds (1 every 6.25 seconds is an attack)
	private DetectionPoint ae4 = new DetectionPoint(DetectionPoint.Category.AUTHENTICATION, "AE4");

	private DetectionSystem detectionSystem = new DetectionSystem("myclientapp");

	@Autowired
	RestEventManager eventManager;
	
	public void execute() {
//		System.err.println("-- " + gson.toJson(bob));
		
		// will definitely see attacks, probably many
		EventEmitter ie1Emitter = new EventEmitter(ie1, 1, 4);
		
		// likely won't see an attack, but possible
		EventEmitter ie2Emitter = new EventEmitter(ie2, 4, 15);
		
		// very likely will see an attack at some point
		EventEmitter re3Emitter = new EventEmitter(re3, 2, 7);
		
		// will only see events, not an attack
		EventEmitter ae4Emitter = new EventEmitter(ae4, 10, 20);
		
		new Thread(ie1Emitter).start();
		new Thread(ie2Emitter).start();
		new Thread(re3Emitter).start();
		new Thread(ae4Emitter).start();
    }
	
	class EventEmitter implements Runnable {

		private DetectionPoint detectionPoint;
		private int lowerBoundSeconds;
		private int upperBoundSeconds;
		
		Random random = new Random();
		
		EventEmitter(DetectionPoint detectionPoint, int lowerBoundSeconds, int upperBoundSeconds) {
			this.detectionPoint = detectionPoint;
			this.lowerBoundSeconds = lowerBoundSeconds;
			this.upperBoundSeconds = upperBoundSeconds;
		}
		
		@Override
		public void run() {
			while(true) {
				sleep(randInt(lowerBoundSeconds, upperBoundSeconds));
				
				System.err.format("Sending event type '%s' from user '%s' and system '%s'%s", 
						detectionPoint.getLabel(), bob.getUsername(), detectionSystem.getDetectionSystemId(), System.getProperty("line.separator"));
				try {
					Event event = new Event(bob, detectionPoint, detectionSystem);
					System.err.println("sending || " + gson.toJson(event) + " ||");
					
					synchronized(AppsensorWsRestClientBootDataGeneratorApplication.mutex) {
						eventManager.updateApplicationIdentificationHeaderValue(detectionSystem.getDetectionSystemId());
						eventManager.addEvent(event);
					}
				} catch(Exception e) {
					System.err.println("Exception type: " + e.getClass().getCanonicalName());
					e.printStackTrace();
				}
			}
		}
		
		private int randInt(int min, int max) {
		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    int randomNum = random.nextInt((max - min) + 1) + min;

		    return randomNum;
		}
		
		private void sleep(int seconds) {
			try {
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
