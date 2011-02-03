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

public class CedalionMenuItem {
	public CedalionMenuItem(Compound term, Menu parent, TermContext context) {
		MenuItem item = new MenuItem(parent, SWT.PUSH);
		item.setText((String) term.arg(1));
		final Compound proc = (Compound) term.arg(2);
		item.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				try {
					ExecutionContext exe = new ExecutionContext(proc.getProlog());
					exe.runProcedure(proc);
				} catch (PrologException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TermInstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionContextException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
