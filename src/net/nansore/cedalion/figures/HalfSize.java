/*
 * Created on Feb 5, 2006
 */
package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.swt.graphics.FontData;

/**
 * @author boaz
 */
public class HalfSize extends FontModifier {

    /**
     * @param term
     * @param context
     * @throws TermVisualizationException
     * @throws PrologException 
     * @throws TermInstantiationException 
     */
    public HalfSize(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, context);
    }

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.FontModifier#modifyFont(org.eclipse.swt.graphics.FontData[], int, net.nansore.visualterm.prolog.Compound)
     */
    protected FontData modifyFont(FontData[] fontData, int i, Compound term) {
        return new FontData(fontData[i].getName(), fontData[i].getHeight() / 2, fontData[i].getStyle());
    }

}
