/*
 * Created on Jan 24, 2006
 */
package net.nansore.cedalion.eclipse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.jface.dialogs.MessageDialog;

/**
 * @author boaz
 */
public class update implements ICommand {

    private int termID;
    private static List<CedalionEditor> editors = new ArrayList<CedalionEditor>();
    
    public update(Compound term) throws ExecutionContextException {
        if(!(term.arg(1) instanceof Integer))
            throw new ExecutionContextException("The argument for an update command must be of integer type");
        termID = ((Integer)term.arg(1)).intValue();
    }
    /* (non-Javadoc)
     * @see net.nansore.visualterm.command.Command#run(org.eclipse.ui.IWorkbenchPart)
     */
    public void run(TermContext context) {
        try {
            for(Iterator<CedalionEditor>  i = editors.iterator(); i.hasNext(); ) {
                i.next().updateFigure(termID);
            }
        } catch (TermVisualizationException e) {
            MessageDialog.openError(context.getWorkbenchPart().getSite().getShell(), "Failed To Visualize Term", e.getMessage());
            e.printStackTrace();
        } catch (TermInstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * @param editor
     */
    public static synchronized void registerEditor(CedalionEditor editor) {
        editors.add(editor);
    }
    /**
     * @param editor
     */
    public static synchronized void unregisterEditor(CedalionEditor editor) {
        editors.remove(editor);
    }
	@Override
	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		// TODO Auto-generated method stub
		
	}

}
