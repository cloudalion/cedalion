package net.nansore.cedalion.figures;

import java.io.IOException;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.execution.PathStore;
import net.nansore.cedalion.execution.PropertyNotFoundException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;

/**
 * An ExpandFigure holds two versions of its contents: "collapsed" and "expanded".
 * It displays both next to an icon with either a "+" (for "collapsed"), or a "-" (for "expanded").
 * Clicking the icon toggles the figure's state.
 * ExpandFigure tries to remember its state from one instance to another based on its path.
 * However, editing the file can shift the path and cause it to loose track of its state.
 * Receives two arguments:
 * 1. Its collapsed contents
 * 2. Its expanded contents
 */
public class ExpandFigure extends TermContextProxy {

	private ImageFigure icon;
	private Panel panel;
	private TermFigure collapsed;
	private TermFigure expanded;
	private Compound path;
	private Compound term;
	private boolean isExpanded = false;

	public ExpandFigure(Compound term, TermContext parent) throws TermInstantiationException, PrologException {
		super(parent);
		this.term = term;
		path = parent.getPath();
		// Build a panel with an icon to its left
		setLayoutManager(new FlowLayout());
		icon = new ImageFigure();
		panel = new Panel();
		panel.setLayoutManager(new FlowLayout());
		add(icon);
		add(panel);
		try {
			String iconName = isExpanded() ? "expanded" : "collapsed";
			icon.setImage(parent.getImage(iconName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(isExpanded()) {
			expanded = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(2), this);			
		} else {
			collapsed = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), this);			
		}
		panel.add(isExpanded() ? expanded : collapsed);
		icon.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent me) {
			}
			
			@Override
			public void mousePressed(MouseEvent me) {
				try {
					toggle();
				} catch (TermInstantiationException e) {
					e.printStackTrace();
				} catch (PrologException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void mouseDoubleClicked(MouseEvent me) {
			}
		});
	}

	protected void toggle() throws TermInstantiationException, PrologException {
		boolean isExpanded = isExpanded();
		IFigure from = isExpanded ? expanded : collapsed;
		IFigure to = isExpanded ? collapsed : expanded;
		if(to == null) {
			if(isExpanded()) {
				to = collapsed = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), this);			
			} else {
				to = expanded = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(2), this);			
			}
		}
		isExpanded = !isExpanded;
		setExpaneded(isExpanded);
		panel.remove(from);
		panel.add(to);
		String iconName = isExpanded ? "expanded" : "collapsed";
		try {
			icon.setImage(getImage(iconName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setExpaneded(boolean isExpanded) {
		if(path != null) {
			PathStore.instance().assign(path, "expanded", isExpanded);
			System.out.println("Assigned to path: " + path + ": " + isExpanded);			
		}
		this.isExpanded = isExpanded;
	}

	private boolean isExpanded() {
		if(path == null)
			return isExpanded;
		try {
			boolean expanded = (Boolean)PathStore.instance().getProperty(path, "expanded");
			System.out.println("Expanded for path " + path + ": " + expanded);
			return expanded;
		} catch (PropertyNotFoundException e) {
			System.out.println("Expanded for path " + path + ": No value");
			return false;
		}
	}

	@Override
	public void dispose() {
		if(expanded != null)
			expanded.dispose();
		if(collapsed != null)
			collapsed.dispose();
		icon.erase();
		panel.erase();
	}

}
