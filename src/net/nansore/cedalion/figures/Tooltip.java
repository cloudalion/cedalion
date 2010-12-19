package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class Tooltip extends TermContextProxy {

	private final class ChangeForeground extends TermContextProxy {
		private final Color tooltipFG;
		private TermFigure figure;

		private ChangeForeground(TermContext parent, Color tooltipFG, Compound compound) throws TermInstantiationException, PrologException {
			super(parent);
			this.tooltipFG = tooltipFG;
			figure = (TermFigure) TermInstantiator.instance().instantiate(compound, this);
			add(figure);
		}

		@Override
		public void dispose() {
			figure.dispose();
		}

		@Override
		public Color getColor() {
			return tooltipFG;
		}
	}

	private TermFigure figure;
	private TermFigure tooltip;

	public Tooltip(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
        setLayoutManager(new FlowLayout());
        figure = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), this);
		add(figure);
		final Color tooltipFG = parent.getCanvas().getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        tooltip = new ChangeForeground(this, tooltipFG, (Compound)term.arg(2));
        setToolTip(tooltip);
	}

	public void dispose() {
		tooltip.dispose();
		figure.dispose();
	}

}
