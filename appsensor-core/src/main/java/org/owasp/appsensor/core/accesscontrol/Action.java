package org.owasp.appsensor.core.accesscontrol;

/**
 * This enum gives the options of the types of actions that can be 
 * performed and for which access control needs to be considered.
 * 
 * This works in conjunction with the {@link AccessController}.
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
public enum Action {
	
	ADD_EVENT,
	ADD_ATTACK,
	GET_RESPONSES,
	EXECUTE_REPORT
	
}
