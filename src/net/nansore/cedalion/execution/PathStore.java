package net.nansore.cedalion.execution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nansore.prolog.Compound;

public class PathStore {
	private static PathStore instance;
	private Map<Compound, Map<String, Object>> propMap = new HashMap<Compound, Map<String,Object>>();

	private PathStore() {
		
	}
	
	public static PathStore instance() {
		if(instance == null)
			instance = new PathStore();
		return instance;
	}
	
	public void assign(Compound path, String name, Object value) {
		Map<String, Object> properties = propMap.get(path);
		if(properties == null) {
			properties = new HashMap<String, Object>();
			propMap.put(path, properties);
		}
		properties.put(name, value);
	}
	
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
