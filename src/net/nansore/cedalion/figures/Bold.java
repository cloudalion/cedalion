/*
 * Created on Jan 27, 2006
 */
package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * @author boaz
 */
public class Bold extends FontModifier {

    /**
     * @param term
     * @param context
     * @throws TermVisualizationException
     * @throws PrologException 
     * @throws TermInstantiationException 
     */
    public Bold(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, context);
    }

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.FontModifier#modifyFont(org.eclipse.swt.graphics.FontData[], int)
     */
    protected FontData modifyFont(FontData[] fontData, int i, Compound term) {
        return new FontData(fontData[i].getName(), (int)fontData[i].height, fontData[i].getStyle()| SWT.BOLD);
    }

}
