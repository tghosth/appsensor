package org.owasp.appsensor.analysis;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.owasp.appsensor.core.AppSensorServer;
import org.owasp.appsensor.core.Attack;
import org.owasp.appsensor.core.Interval;
import org.owasp.appsensor.core.Response;
import org.owasp.appsensor.core.analysis.AttackAnalysisEngine;
import org.owasp.appsensor.core.criteria.SearchCriteria;
import org.owasp.appsensor.core.logging.Loggable;
import org.owasp.appsensor.core.rule.Rule;
import org.owasp.appsensor.core.storage.AttackStore;
import org.owasp.appsensor.core.storage.ResponseStore;
import org.slf4j.Logger;

/**
 * This is the rule based {@link Attack} analysis engine,
 * and is an implementation of the Observer pattern.
 *
 * It is notified with implementations of the {@link Attack} class.
 *
 * The implementation performs a simple analysis that checks the created attack against any created {@link Response}s.
 * It then creates a {@link Response} and adds it to the {@link ResponseStore}.
 *
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 * @author David Scrobonia (davidscrobonia@gmail.com)
 */
@Named
@Loggable
public class AggregateAttackAnalysisEngine extends AttackAnalysisEngine {

	private Logger logger;

	@Inject
	private AppSensorServer appSensorServer;

	/**
	 * This method analyzes {@link Attack} objects that are added
	 * to the system (either via direct addition or generated by the event analysis
	 * engine), generates an appropriate {@link Response} object,
	 * and adds it to the configured {@link ResponseStore}
	 *
	 * @param event the {@link Attack} that was added to the {@link AttackStore}
	 */
	@Override
	public void analyze(Attack attack) {
		if (attack != null && attack.getRule() != null) {
			Response response = findAppropriateResponse(attack);

			if (response != null) {
				logger.info("Response set for user <" + attack.getUser().getUsername() + "> - storing response action " + response.getAction());
				appSensorServer.getResponseStore().addResponse(response);
			}
		}
	}

	/**
	 * Find/generate {@link Response} appropriate for specified {@link Attack}.
	 *
	 * @param attack {@link Attack} that is being analyzed
	 * @return {@link Response} to be executed for given {@link Attack}
	 */
	protected Response findAppropriateResponse(Attack attack) {
		Rule triggeringRule = attack.getRule();

		SearchCriteria criteria = new SearchCriteria().
				setUser(attack.getUser()).
				setRule(triggeringRule).
				setDetectionSystemIds(appSensorServer.getConfiguration().getRelatedDetectionSystems(attack.getDetectionSystem()));

		//grab any existing responses
		Collection<Response> existingResponses = appSensorServer.getResponseStore().findResponses(criteria);

		String responseAction = null;
		Interval interval = null;

		Collection<Response> possibleResponses = findPossibleResponses(triggeringRule);

		if (existingResponses == null || existingResponses.size() == 0) {
			//no responses yet, just grab first configured response from rule
			Response response = possibleResponses.iterator().next();

			responseAction = response.getAction();
			interval = response.getInterval();
		} else {
			for (Response configuredResponse : possibleResponses) {
				responseAction = configuredResponse.getAction();
				interval = configuredResponse.getInterval();

				if (! isPreviousResponse(configuredResponse, existingResponses)) {
					//if we find that this response doesn't already exist, use it
					break;
				}

				//if we reach here, we will just use the last configured response (repeat last response)
			}
		}

		if(responseAction == null) {
			throw new IllegalArgumentException("No appropriate response was configured for this rule: " + triggeringRule.getName());
		}

		Response response = new Response().
				setUser(attack.getUser()).
				setTimestamp(attack.getTimestamp()).
				setAction(responseAction).
				setInterval(interval).
				setDetectionSystem(attack.getDetectionSystem());

		return response;
	}

	/**
	 * Lookup configured {@link Response} objects for specified {@link Rule}
	 *
	 * @param rule triggered {@link Rule}
	 * @return collection of {@link Response} objects for given {@link Rule}
	 */
	protected Collection<Response> findPossibleResponses(Rule rule) {
		Collection<Response> possibleResponses = new ArrayList<Response>();

		for (Rule configuredRule : appSensorServer.getConfiguration().getRules()) {
			if (configuredRule.equals(rule)) {
				possibleResponses = configuredRule.getResponses();
				break;
			}
		}

		return possibleResponses;
	}

	/**
	 * Test a given {@link Response} to see if it's been executed before.
	 *
	 * @param response {@link Response} to test to see if it's been executed before
	 * @param existingResponses set of previously executed {@link Response}s
	 * @return true if {@link Response} has been executed before
	 */
	protected boolean isPreviousResponse(Response response, Collection<Response> existingResponses) {
		boolean previousResponse = false;

		for (Response existingResponse : existingResponses) {
			if (response.getAction().equals(existingResponse.getAction())) {
				previousResponse = true;
			}
		}

		return previousResponse;
	}

}
