package org.owasp.appsensor.core.analysis;

import org.owasp.appsensor.core.Attack;
import org.owasp.appsensor.core.listener.AttackListener;
import org.owasp.appsensor.core.storage.AttackStore;
import org.owasp.appsensor.core.storage.AttackStoreListener;

/**
 * The attack analysis engine is an implementation of the Observer pattern. 
 * 
 * In this case the analysis engines watches the {@link AttackStore} interface.
 * 
 * AnalysisEngine implementations are the components of AppSensor that 
 * constitute the "brain" of the system. 
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 * @author Raphaël Taban
 */
@AttackStoreListener
public abstract class AttackAnalysisEngine implements AttackListener {
	
	public void onAdd(Attack attack) {
		analyze(attack);
	}
	
	public abstract void analyze(Attack attack);
	
}
