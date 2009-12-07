package net.nansore.cedalion.execution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

public class ExecutionContext {

	private PrologProxy prolog;
	private Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

	public ExecutionContext(PrologProxy p) {
		prolog = p;
	}

	public void runProcedure(Compound proc) throws PrologException, TermInstantiationException {
		Variable command = new Variable("Command");
		Map<Variable, Object> result = prolog.getSolution(new Compound("cedalion#procedureCommand", proc, command));
		try {
			runCommand((Compound)result.get(command));
		} catch (ClassCastException e) {
			throw new PrologException("Command returned from procedure " + proc + " is not a compound term");
		}
	}

	private void runCommand(Compound commandTerm) throws PrologException, TermInstantiationException {
		ICommand command;
		try {
			command = (ICommand) instantiate(commandTerm);
		} catch (ClassCastException e) {
			throw new PrologException("Not a command: " + commandTerm);
		}
		command.run(this);
		
	}

	private Object instantiate(Compound term) throws TermInstantiationException, PrologException {
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
			throw new TermInstantiationException(e);
		}
		
	}

	private Class<?> termClass(Compound term) throws PrologException, TermInstantiationException {
		Class<?> clazz = classCache.get(term.name());
		if(clazz == null) {
			Variable classNameVar = new Variable("VarName");
			Compound query = new Compound("cedalion#termClass", new Compound("::", term, new Variable("T")), classNameVar);
			Map<Variable, Object> result = prolog.getSolution(query);
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
