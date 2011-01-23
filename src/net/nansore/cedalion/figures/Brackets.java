package net.nansore.cedalion.figures;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class Brackets extends Panel implements TermFigure {

	private static final double MODIFICATION_THRESHOLD = 0.1;
	private Label openning;
	private Label closing;
	private TermFigure content;
	private TermContext context;

	public Brackets(Compound term, TermContext context) throws TermInstantiationException, PrologException {
		setLayoutManager(new FlowLayout());
		context.bindFigure(this);
		StringBuffer openningBracket = new StringBuffer();
		StringBuffer closingBracket = new StringBuffer();
		content = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), context);
		openningBracket.append((char)((Integer)term.arg(2)).intValue());
		closingBracket.append((char)((Integer)term.arg(3)).intValue());
		openning = new Label(openningBracket.toString());
		openning.setFont(context.getFont(TermContext.SYMBOL_FONT));
		closing = new Label(closingBracket.toString());
		closing.setFont(context.getFont(TermContext.SYMBOL_FONT));
		add(openning);
		add(content);
		add(closing);
		this.context = context;
	}

	@Override
	public void updateFigure() throws TermVisualizationException,
			TermInstantiationException {
	}

	@Override
	public void dispose() {
		content.dispose();
	}

	@Override
	public void validate() {
		super.validate();
		Dimension contentDim = content.getSize();
		Dimension openningDim = openning.getSize();
		float ratio = (float)contentDim.height/(float)openningDim.height;
		if(Math.abs(ratio - 1.0) < MODIFICATION_THRESHOLD)
			return;
		Font newFont = resizeFont(openning.getFont(), ratio);
		openning.setFont(newFont);
		closing.setFont(newFont);
	}

	private Font resizeFont(Font font, float ratio) {
		FontData[] oldFontData = font.getFontData();
		FontData[] newFontData = new FontData[oldFontData.length];
		for(int i = 0; i < oldFontData.length; i++) {
			float newHeight = oldFontData[i].height * ratio;
			newFontData[i] = new FontData(oldFontData[i].name, Math.round(newHeight), oldFontData[i].style);
		}
		return FontModifier.createFont(context, newFontData);
	}

}
