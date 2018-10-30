package org.owasp.appsensor.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Resource represents a generic component of an application. In many cases, 
 * it would represent a URL, but it could also presumably be used for something 
 * else, such as a specific object, function, or even a subsection of an application, etc.
 * 
 * @see java.io.Serializable
 *
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
@Entity
public class Resource implements IAppsensorEntity {
	
	private static final long serialVersionUID = 343899601431699577L;

	@Id
	@Column(columnDefinition = "integer")
	@GeneratedValue
	private String id;

	/** 
	 * The resource being requested when a given event/attack was triggered, which can be used 
     * later to block requests to a given function.  In this implementation, 
     * the current request URI is used.
     */
	@Column
	private String location;

	/**
	 * The method used to request the resource. In terms of HTTP this would be GET/POST/PUT/etc.
	 * In the case, in which the resources specifies an object this could be the invoked object method.
	 */
	@Column
	private String method;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
