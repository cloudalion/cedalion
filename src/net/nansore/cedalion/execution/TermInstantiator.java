package net.nansore.cedalion.execution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.nansore.prolog.Compound;
import net.nansore.prolog.NoSolutionsException;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

public class TermInstantiator {
	
	private Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
	private static TermInstantiator instance = new TermInstantiator();
	
	private TermInstantiator() {
		
	}

	public static TermInstantiator instance() {
		return instance;
	}
	
	public synchronized Object instantiate(Compound term) throws TermInstantiationException, PrologException {
		Class<?> clazz = termClass(term);
		try {
			Constructor<?> constructor = clazz.getConstructor(Compound.class);
			return constructor.newInstance(term);
		} catch (SecurityException e) {
			throw new TermInstantiationException(e);
		} catch (NoSuchMethodException e) {
			throw new TermInstantiationException(e);
		} catch (IllegalArgumentException e) {
			throw new TermInstantiationException(e);
		} catch (InstantiationException e) {
			throw new TermInstantiationException(e);
		} catch (IllegalAccessException e) {
			throw new TermInstantiationException(e);
		} catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof TermInstantiationException)
				throw (TermInstantiationException) e.getTargetException();
			if(e.getTargetException() instanceof PrologException)
				throw (PrologException) e.getTargetException();
			throw new TermInstantiationException(e);
		}		
	}

	private Class<?> termClass(Compound term) throws PrologException, TermInstantiationException {
		PrologProxy prolog = term.getProlog();
		Class<?> clazz = classCache.get(term.name());
		if(clazz == null) {
			Variable classNameVar = new Variable("VarName");
			Compound query = new Compound(prolog, "cedalion#termClass", new Compound(prolog, "::", term, new Variable("T")), classNameVar);
			Map<Variable, Object> result;
			try {
				result = prolog.getSolution(query);
			} catch (NoSolutionsException e) {
				throw new TermInstantiationException("Term " + term + " is not associated with a class");
			}
			String className = (String) result.get(classNameVar);
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new TermInstantiationException(e);
			}
		}
		return clazz;
	}

}
