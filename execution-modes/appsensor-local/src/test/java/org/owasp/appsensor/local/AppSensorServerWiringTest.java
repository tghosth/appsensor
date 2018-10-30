package org.owasp.appsensor.local;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.appsensor.core.AppSensorServer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:base-context.xml"})
public class AppSensorServerWiringTest {

	@Inject
	private AppSensorServer appSensorServer;
	
	@Test
	public void testInstantiation() {
		assertNotNull("Server instance is null", appSensorServer);
		assertNotNull("Event store cannot is null", appSensorServer.getEventStore());
		assertNotNull("Attack store cannot is null", appSensorServer.getAttackStore());
		assertNotNull("Response store cannot is null", appSensorServer.getResponseStore());
		assertNotNull("EventAnalysisEngine store cannot is null", appSensorServer.getEventAnalysisEngines());
		assertNotNull("AttackAnalysisEngine store cannot is null", appSensorServer.getAttackAnalysisEngines());
		assertNotNull("ClientApplicationIdentificationHeaderName cannot be null", appSensorServer.getConfiguration().getClientApplicationIdentificationHeaderName());
	}
}