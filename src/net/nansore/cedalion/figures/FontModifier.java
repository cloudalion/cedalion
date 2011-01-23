/*
 * Created on Jan 27, 2006
 */
package net.nansore.cedalion.figures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author boaz
 */
abstract public class FontModifier extends TermContextProxy {
    private Font normalFont;
    private TermFigure figure;
	private Font symbolFont;
    private static Map<String, FontElement> fontRegistry = new HashMap<String, FontElement>();

    /**
     * @param parent
     * @throws TermVisualizationException
     * @throws PrologException 
     * @throws TermInstantiationException 
     */
    public FontModifier(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(context);

        FontData[] fontData = context.getFont(NORMAL_FONT).getFontData();
        FontData[] newFontData = new FontData[fontData.length];
        for(int i = 0; i < fontData.length; i++) {
            newFontData[i] = modifyFont(fontData, i, term);
        }
        normalFont = createFont(context, newFontData);
        
        fontData = context.getFont(SYMBOL_FONT).getFontData();
        newFontData = new FontData[fontData.length];
        for(int i = 0; i < fontData.length; i++) {
            newFontData[i] = modifyFont(fontData, i, term);
        }
        symbolFont = createFont(context, newFontData);
        
        try {
            figure = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), this); 
            add(figure);
        } catch (ClassCastException e) {
            throw new TermVisualizationException("A font modifier must have a compound child");
        }
   }

    /**
     * @param fontData
     * @param i
     * @param term 
     * @return
     */
    protected abstract FontData modifyFont(FontData[] fontData, int i, Compound term) ;

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.TermFigure#updateFigure(int)
     */
    public void updateFigure() throws TermVisualizationException {
    }

    public Font getFont(int fontType) {
    	Font font = fontType == TermContext.NORMAL_FONT ? normalFont : symbolFont;
	    if(font == null) {
	        throw new RuntimeException("Figure already disposed");
	    }
	    return font;
    }

    /**
     * Implementation of the font registry
     */
    private static class FontElement {
        public Font font;
        public int refs = 1;
        
        public FontElement(Font font) {
            this.font = font;
        }
    }
    
    public static synchronized Font createFont(TermContext context, FontData[] fontData) {
        String fontDataDesc = fontDataDescription(fontData);
        FontElement element = fontRegistry.get(fontDataDesc);
        if(element == null) {
            Font font = new Font(context.getTextEditor().getDisplay(), fontData);
            element = new FontElement(font);
            fontRegistry.put(fontDataDesc, element);
            System.out.println("Created font: " + fontDataDesc);
        } else {
            element.refs++;
        }
        return element.font;
    }
    
    /*    private static synchronized void disposeFont(Font font) {
        String fontDataDesc = fontDataDescription(font.getFontData());
        FontElement element = (FontElement) fontRegistry.get(fontDataDesc);
        if(element == null) {
        	System.err.println("Could not dispose font: " + fontDataDesc);
        	return;
        }
        element.refs--;
        if(element.refs == 0) {
            element.font.dispose();
            fontRegistry.remove(fontDataDesc);
            System.out.println("Removing font: " + fontDataDesc);
        }
    }*/

    /**
     * @param fontData
     * @return
     */
    private static String fontDataDescription(FontData[] fontData) {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < fontData.length; i++) {
            buff.append(fontData[i].toString() + ";");
        }
        String fontDataDesc = buff.toString();
        return fontDataDesc;
    }
	public void dispose() {
		figure.dispose();
	}
}
