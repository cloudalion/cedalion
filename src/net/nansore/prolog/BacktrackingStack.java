package net.nansore.prolog;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a thread-local mechanism for allowing backtracking of variable bindings.
 * It contains a stack of changes.  The position on the stack can be queried using 
 * getState(), and then backtrack() can roll-back all variable bindings performed
 * since that state.
 */
public class BacktrackingStack {
	private static ThreadLocal<BacktrackingStack> tls = new ThreadLocal<BacktrackingStack>() {
		@Override protected BacktrackingStack initialValue() {
			return new BacktrackingStack();
		}
	};
	
	public static class Frame {
		private Variable var;
		private Object val;

		public Frame(Variable var, Object val) {
			this.var = var;
			this.val = val;
		}
	}
	
	private List<Frame> stack = new ArrayList<BacktrackingStack.Frame>();
	
	/**
	 * Add a variable binding to the stack
	 * @param var the variable that has been bound
	 * @param val its previous (pre-binding) value
	 */
	public void push(Variable var, Object val) {
		stack.add(new Frame(var, val));
	}
	
	/**
	 * Roll back the most recently-made binding on this stack.
	 */
	public void pop() {
		Frame frame = stack.get(stack.size()-1);
		frame.var.setValue(frame.val);
		stack.remove(stack.size()-1);
	}
	
	/**
	 * @return an instance of the stack relevant for this thread.
	 */
	public static BacktrackingStack instance() {
		return tls.get();
	}
	
	/**
	 * @return the current state of the stack on the current thread
	 */
	public static int getState() {
		return instance().stack.size();
	}
	
	/**
	 * Rolls back all variable bindings made on the current thread since state has been received from getState()
	 * @param state the state to roll-back to.
	 */
	public static void backtrack(int state) {
		BacktrackingStack instance = instance(); 
		while(instance.stack.size() > state) {
			instance.pop();
		}
	}
}
