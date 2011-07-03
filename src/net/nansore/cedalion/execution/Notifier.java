package net.nansore.cedalion.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nansore.prolog.Compound;

/**
 * This is a singleton class providing a notification mechanism for events associated with Cedalion code elements.
 * It allows Runnable objects to be associated to Cedalion terms, intended to represent paths to code elements. 
 */
public class Notifier {
	private static Notifier instance;
	private Map<Compound, List<Runnable>> cmdMap = new HashMap<Compound, List<Runnable>>();;

	private Notifier() {
		
	}
	
	/**
	 * @return the singleton instance
	 */
	public static Notifier instance() {
		if(instance == null)
			instance = new Notifier();
		return instance;
	}
	
	/**
	 * Registers an action to a path.
	 * @param term the path to associated the action to
	 * @param command the action itself
	 * @return a Runnable providing the un-registration action
	 */
	public synchronized Runnable register(final Compound term, final Runnable command) {
		List<Runnable> l = cmdMap.get(term);
		if(l == null) {
			l = new ArrayList<Runnable>();
			cmdMap.put(term, l);
		}
		l.add(command);
		return new Runnable() {
			@Override
			public void run() {
				cmdMap.get(term).remove(command);
			}
		};
	}
	
	/**
	 * Activates the actions associated with the given path
	 * @param term the path
	 */
	public synchronized void notify(Compound term) {
		List<Runnable> l = cmdMap.get(term);
		if(l != null) {
			for(Runnable r : l) {
				r.run();
			}
		}
	}

	/**
	 * Debugging: Prints the number of elements registered.
	 */
	public void printRefCount() {
		int total = 0;
		for(List<Runnable> l : cmdMap.values()) {
			total += l.size();
		}
		System.out.println("Notifiables: " + total);
	}
}
