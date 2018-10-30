package org.owasp.appsensor.core;


import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.net.InetAddresses;

/**
 * Identifier label for the system that detected the event. 
 * This will be either the client application, or possibly an external 
 * detection system, such as syslog, a WAF, network IDS, etc.  
 * 
 * @see java.io.Serializable
 *
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class DetectionSystem implements IAppsensorEntity {

	private static final long serialVersionUID = -9213994652294519363L;

	@Id
	@Column(columnDefinition = "integer")
	@GeneratedValue
	private String id;
	
	private String detectionSystemId;
	
	@JsonProperty("ipAddress")
	private IPAddress ipAddress;
	
	@Inject
	private transient IPAddress locator;

	public DetectionSystem() {}
	
	public DetectionSystem(String detectionSystemId) {
		setDetectionSystemId(detectionSystemId);
	}
	
	public DetectionSystem(String detectionSystemId, IPAddress ipAddress) {
		setDetectionSystemId(detectionSystemId);
		setIPAddress(ipAddress);
	}
	
	public String getDetectionSystemId() {
		return detectionSystemId;
	}

	public DetectionSystem setDetectionSystemId(String detectionSystemId) {
		this.detectionSystemId = detectionSystemId;
		
		// if IP is used as system id, setup IP address w/ geolocation
		if (locator != null && InetAddresses.isInetAddress(detectionSystemId)) {
			this.ipAddress = locator.fromString(detectionSystemId);
		}
		
		return this;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("ipAddress")
	public IPAddress getIPAddress() {
		return ipAddress;
	}

	@JsonProperty("ipAddress")
	public DetectionSystem setIPAddress(IPAddress ipAddress) {
		this.ipAddress = ipAddress;
		
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,31).
				append(detectionSystemId).
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
		
		DetectionSystem other = (DetectionSystem) obj;
		
		return new EqualsBuilder().
				append(detectionSystemId, other.getDetectionSystemId()).
				isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).
				append("detectionSystemId", detectionSystemId).
				append("ipAddress", ipAddress).
			    toString();
	}
	
}
