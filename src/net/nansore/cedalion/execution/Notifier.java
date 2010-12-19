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
	
	public synchronized void notify(Compound term) {
		List<Runnable> l = cmdMap.get(term);
		if(l != null) {
			for(Runnable r : l) {
				r.run();
			}
		}
	}

	public void printRefCount() {
		int total = 0;
		for(List<Runnable> l : cmdMap.values()) {
			total += l.size();
		}
		System.out.println("Notifiables: " + total);
	}
}
