package net.nansore.cedalion.execution;

import net.nansore.prolog.Compound;

public class PropertyNotFoundException extends Exception {

	public PropertyNotFoundException(Compound path, String name) {
		super("No property " + name + " assiciated with path " + path);
	}

}
