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
	
	public synchronized Object instantiate(Object... args) throws TermInstantiationException, PrologException {
		if(!(args[0] instanceof Compound))
			throw new TermInstantiationException("First argument must be a compound term");
		Class<?> clazz = termClass((Compound)args[0]);
		try {
			Constructor<?> constructor = findConstructorFor(clazz, args);
			return constructor.newInstance(args);
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

	private static Constructor<?> findConstructorFor(Class<?> clazz,
			Object[] args) throws NoSuchMethodException {
		Constructor<?>[] constructors = clazz.getConstructors();
		for(Constructor<?> c : constructors) {
			Class<?>[] argTypes = c.getParameterTypes();
			if(argTypes.length != args.length)
				continue;
			if(argsMatchTypes(args, argTypes))
				return c;
		}
		throw new NoSuchMethodException(clazz.toString());
	}

	private static boolean argsMatchTypes(Object[] args, Class<?>[] argTypes) {
		for(int i = 0; i < args.length; i++) {
			if(!argTypes[i].isAssignableFrom(args[i].getClass()))
				return false;
		}
		return true;
	}

	private Class<?> termClass(Compound term) throws PrologException, TermInstantiationException {
		PrologProxy prolog = term.getProlog();
		Class<?> clazz = classCache.get(term.name());
		if(clazz == null) {
			Variable classNameVar = new Variable("VarName");
			Compound query = new Compound(prolog, "cpi#termClass", new Compound(prolog, "::", term, new Variable("T")), classNameVar);
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
