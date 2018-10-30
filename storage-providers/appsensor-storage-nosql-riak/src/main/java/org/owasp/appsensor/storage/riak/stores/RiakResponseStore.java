package org.owasp.appsensor.storage.riak.stores;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.datatypes.FetchSet;
import com.basho.riak.client.api.commands.datatypes.SetUpdate;
import com.basho.riak.client.api.commands.datatypes.UpdateSet;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakSet;
import com.basho.riak.client.core.util.BinaryValue;
import com.google.gson.Gson;
import org.owasp.appsensor.core.Response;
import org.owasp.appsensor.core.criteria.SearchCriteria;
import org.owasp.appsensor.core.listener.ResponseListener;
import org.owasp.appsensor.core.logging.Loggable;
import org.owasp.appsensor.core.storage.ResponseStore;
import org.owasp.appsensor.storage.riak.RiakConstants;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import java.util.concurrent.ExecutionException;

/**
 * This is a Riak implementation of the {@link ResponseStore}.
 *
 * Implementations of the {@link ResponseListener} interface can register with
 * this class and be notified when new {@link Response}s are added to the data store
 *
 * <p>
 *     This implementation add and fetch responses from set with key "responses" in bucket "appsensor"
 *     Therefore you have to enable "sets" bucket type on your riak server
 *     <ul>
 *          <li>riak-admin bucket-type create sets '{"props":{"datatype":"set"}}'</li>
 *          <li>riak-admin bucket-type activate sets</li>
 *      </ul>
 * </p>
 * <p>Note: This class requires a few settings to run properly. These can be set as either
 *    environment variables ('export my_var="some_value"') or environment
 *    properties ('-Dmy_var=some_value')</p>
 * <ul>
 *   <li><em>RIAK_SERVER_ADDRESS</em> - comma separated riak server addresses list, e.g. "192.168.1.1,192.168.1.2"</li>
 *   <li><em>RIAK_SERVER_PORT</em> - riak server port, e.g. "8087"</li>
 * </ul>
 * <p>
 *     {@link #initializeConnection()} method can be overridden if custom initialization is needed.
 * </p>
 *
 * @author Robert Przystasz  (robert.przystasz@gmail.com)
 * @author Bartosz Wyględacz (bartosz.wygledacz@gmail.com)
 * @author Michal Warzecha   (mwarzechaa@gmail.com)
 * @author Magdalena Idzik   (maddie@pwnag3.net)
 */
@Named
@Loggable
public class RiakResponseStore extends ResponseStore implements RiakConstants {

	private Logger logger;

	@Inject
	private Environment environment;

	private Location responses;

	private RiakClient client;

	private Gson gson = new Gson();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addResponse(Response response) {
		logger.warn("Security response " + response.getAction() + " triggered for user: " + response.getUser().getUsername());

		String json = gson.toJson(response);

		try {
			client.execute(
					new UpdateSet.Builder(
							responses, new SetUpdate().add(json)
					).build()
			);
		} catch (ExecutionException e) {
			if (logger != null) {
				logger.error("Adding response to RiakDB failed", e);
			}
			e.printStackTrace();
		} catch (InterruptedException e) {
			if (logger != null) {
				logger.error("Adding response to RiakDB was interrupted", e);
			}
			e.printStackTrace();
		}

		super.notifyListeners(response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Response> findResponses(SearchCriteria criteria) {
		return findResponses(criteria, loadResponses());
	}

	private Collection<Response> loadResponses() {
		Collection<Response> responsesCollection = new ArrayList<>();
		try {
			RiakSet set = client.execute(
					new FetchSet.Builder(responses)
							.build()
			).getDatatype();

			for(BinaryValue binaryValue: set.view()) {
				Response response = gson.fromJson(binaryValue.toStringUtf8(), Response.class);
				responsesCollection.add(response);
				}
		} catch (ExecutionException e) {
			logger.error("Fetching responses from RiakDB failed", e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Fetching responses from RiakDB was interrupted", e);
			e.printStackTrace();
		}

		return responsesCollection;
	}

	@PostConstruct
	private void initializeRiak() {
		client = initializeConnection();

		if (client == null) {
			client = defaultInitialize();
		}

		responses = new Location(new Namespace("sets", "appsensor"), "responses");
	}

	@PreDestroy
	private void destroyClient() {
		client.shutdown();
	}

	private RiakClient defaultInitialize() {
		RiakClient client = null;
		try {
			String addresses = environment.getProperty(RIAK_SERVER_ADDRESS);
			int port = Integer.parseInt(environment.getProperty(RIAK_SERVER_PORT));
			client = RiakClient.newClient(port, addresses.split(","));
		} catch (UnknownHostException e) {
			if (logger != null) {
				logger.error("Riak connection could not be made", e);
			}
			e.printStackTrace();
		} catch (NumberFormatException e) {
			if (logger != null) {
				logger.warn("Riak connection could not be made. Port configuration is missing or invalid '{}'", environment.getProperty(RIAK_SERVER_PORT));
			}
		}
		return client;
	}

	/**
	 * Default implementation - override if you want a custom initializer.
	 *
	 * @return RiakClient you want to write to.
	 */
	public RiakClient initializeConnection() {
		return null;
	}
}
