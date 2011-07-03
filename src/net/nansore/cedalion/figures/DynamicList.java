package net.nansore.cedalion.figures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.BacktrackingStack;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Panel;

public class DynamicList extends Panel implements TermFigure {

	private List<TermFigure> figures = new ArrayList<TermFigure>();

	public DynamicList(Compound term, TermContext parent) throws PrologException, TermInstantiationException {
		setLayoutManager(new FlowLayout(false));
		Compound goal = (Compound) term.arg(2);
		Iterator<Map<Variable, Object>> result = PrologProxy.instance().getSolutions(goal);
		while(result.hasNext()) {
			int state = BacktrackingStack.getState();
			try {
				bindVariables(result.next());
				Object figureTemplate = term.arg(1);
				if(figureTemplate instanceof Variable) {
					figureTemplate = ((Variable) figureTemplate).boundTo();
				}
				TermFigure figure = (TermFigure) TermInstantiator.instance().instantiate(figureTemplate, parent);
				figures.add(figure);
				add(figure);
			} finally {
				BacktrackingStack.backtrack(state);
			}
		}
	}

	public static void bindVariables(Map<Variable, Object> varMap) {
		for(Variable var : varMap.keySet()) {
			var.bind(varMap.get(var));
		}
	}

	@Override
	public void dispose() {
		for(TermFigure fig : figures) {
			fig.dispose();
		}
	}

	@Override
	public void updateFigure() throws TermVisualizationException {
		// Do nothing
	}

}
