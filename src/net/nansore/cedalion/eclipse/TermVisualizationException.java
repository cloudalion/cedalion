/*
 * Created on Jan 19, 2006
 */
package net.nansore.cedalion.eclipse;

/**
 * @author boaz
 */
public class TermVisualizationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @param e
     */
    public TermVisualizationException(Throwable e) {
        super(e);
    }

    /**
     * @param string
     */
    public TermVisualizationException(String string) {
        super(string);
    }

}
