package net.nansore.cedalion.eclipse;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MenuItemFactory {

	public static void createItem(Menu menu, Compound action, TermContext context) {
		if(action.name().equals("cpi#item") && action.arity() == 2)
			createMenuItem(menu, action.arg(1), action.arg(2), context);
	}

	private static void createMenuItem(Menu menu, Object name, final Object action, final TermContext context) {
		try {
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(name.toString());
			item.addListener(SWT.Selection, new Listener() {
			    public void handleEvent(Event e) {
					ExecutionContext exe = new ExecutionContext(Activator.getProlog());
					try {
						exe.runProcedure((Compound)action);
					} catch (PrologException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TermInstantiationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionContextException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    }});
		} catch (ClassCastException e1) {
			e1.printStackTrace();
		}
		
	}

}
