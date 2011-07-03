package net.nansore.cedalion.execution;

import java.util.HashMap;
import java.util.Map;

import net.nansore.prolog.Compound;

/**
 * This is a singleton class intended to store application-specific information associated with code-element paths.
 */
public class PathStore {
	private static PathStore instance;
	private Map<Compound, Map<String, Object>> propMap = new HashMap<Compound, Map<String,Object>>();

	private PathStore() {
		
	}
	
	/**
	 * @return the singleton instance
	 */
	public static PathStore instance() {
		if(instance == null)
			instance = new PathStore();
		return instance;
	}
	
	/**
	 * Associate a value with a path and a name 
	 * @param path the path to associate something with
	 * @param name the name to which the value is assigned
	 * @param value the value to assign
	 */
	public void assign(Compound path, String name, Object value) {
		Map<String, Object> properties = propMap.get(path);
		if(properties == null) {
			properties = new HashMap<String, Object>();
			propMap.put(path, properties);
		}
		properties.put(name, value);
	}
	
	/**
	 * Retrieve a value associated with a path and a name
	 * @param path the associated path
	 * @param name the associated name
	 * @return the value associated with the path and name
	 * @throws PropertyNotFoundException if no such value exists
	 */
	public Object getProperty(Compound path, String name) throws PropertyNotFoundException {
		Map<String, Object> properties = propMap.get(path);
		if(properties == null) {
			throw new PropertyNotFoundException(path, name);
		}
		Object value = properties.get(name);
		if(value == null) {
			throw new PropertyNotFoundException(path, name);
		}
		return value;
	}

}
