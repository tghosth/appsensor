package org.owasp.appsensor.core.configuration.client;

import java.io.File;

import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents the configuration for client-side components. 
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
public class ClientConfiguration {

	@Transient
	private File configurationFile;
	
	/** Server connection with configuration info for rest/soap connections */
	private ServerConnection serverConnection;
	
	public File getConfigurationFile() {
		return configurationFile;
	}

	public ClientConfiguration setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
		return this;
	}
	
	public ServerConnection getServerConnection() {
		return serverConnection;
	}

	public ClientConfiguration setServerConnection(ServerConnection serverConnection) {
		this.serverConnection = serverConnection;
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,31).
				append(serverConnection).
				toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		ClientConfiguration other = (ClientConfiguration) obj;
		
		return new EqualsBuilder().
				append(serverConnection, other.getServerConnection()).
				isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).
				append("serverConnection", serverConnection).
			    toString();
	}

}
