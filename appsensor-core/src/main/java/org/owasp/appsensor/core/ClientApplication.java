package org.owasp.appsensor.core;

import java.util.ArrayList;
import java.util.Collection;

import org.owasp.appsensor.core.accesscontrol.Role;

/**
 * The ClientApplication object represents a consumer of the AppSensor 
 * services in any of the client-server style setups.
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
public class ClientApplication {
	
	/** The name of the client application */
	private String name;
	
	/** The collection of {@link Role}s associated with this client application */
	private Collection<Role> roles = new ArrayList<Role>();

	/** The {@link IPAddress} of the client application, optionally set in the server configuration */
	private IPAddress ipAddress;
	
	public String getName() {
		return name;
	}

	public ClientApplication setName(String name) {
		this.name = name;
		return this;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public IPAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(IPAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
