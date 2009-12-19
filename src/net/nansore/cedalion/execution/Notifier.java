package net.nansore.cedalion.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nansore.prolog.Compound;

public class Notifier {
	private static Notifier instance;
	private Map<Compound, List<Runnable>> cmdMap = new HashMap<Compound, List<Runnable>>();;

	private Notifier() {
		
	}
	
	public static Notifier instance() {
		if(instance == null)
			instance = new Notifier();
		return instance;
	}
	
	public synchronized void register(Compound term, Runnable command) {
		List<Runnable> l = cmdMap.get(term);
		if(l == null) {
			l = new ArrayList<Runnable>();
			cmdMap.put(term, l);
		}
		l.add(command);
	}
	
	public synchronized void notify(Compound term) {
		List<Runnable> l = cmdMap.get(term);
		if(l != null) {
			for(Runnable r : l) {
				r.run();
			}
		}
	}
}
