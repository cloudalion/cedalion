package net.nansore.cedalion.figures;

import java.io.IOException;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;

import net.nansore.cedalion.eclipse.Activator;
import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class ExpandFigure extends TermContextProxy {

	private ImageFigure icon;
	private Panel panel;
	private boolean isExpanded;
	private IFigure collapsed;
	private IFigure expanded;

	public ExpandFigure(Compound term, TermContext parent) throws TermInstantiationException, PrologException {
		super(parent);
		// Build a panel with an icon to its left
		setLayoutManager(new FlowLayout());
		icon = new ImageFigure();
		panel = new Panel();
		panel.setLayoutManager(new FlowLayout());
		add(icon);
		add(panel);
		try {
			icon.setImage(parent.getImage("collapsed"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		collapsed = (IFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), this);
		expanded = (IFigure) TermInstantiator.instance().instantiate((Compound)term.arg(2), this);
		isExpanded = false;
		panel.add(collapsed);
		icon.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent me) {
			}
			
			@Override
			public void mousePressed(MouseEvent me) {
				toggle();
			}
			
			@Override
			public void mouseDoubleClicked(MouseEvent me) {
			}
		});
	}

	protected void toggle() {
		IFigure from = isExpanded ? expanded : collapsed;
		IFigure to = isExpanded ? collapsed : expanded;
		isExpanded = !isExpanded;
		panel.remove(from);
		panel.add(to);
		String iconName = isExpanded ? "expanded" : "collapsed";
		try {
			icon.setImage(getImage(iconName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
