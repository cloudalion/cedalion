package net.nansore.cedalion.execution;

import net.nansore.prolog.Compound;

/**
 * An exception thrown if a property is not associated with a path
 * @author boaz
 *
 */
public class PropertyNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PropertyNotFoundException(Compound path, String name) {
		super("No property " + name + " assiciated with path " + path);
	}

}
