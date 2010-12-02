package net.nansore.cedalion.figures;

import java.util.HashMap;
import java.util.Map;

import net.nansore.cedalion.eclipse.Activator;
import net.nansore.cedalion.eclipse.CedalionEditor;
import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

public class Link extends TermContextProxy {

	private String fileName;
	private TermContext context;
	static private Map<String, String> fileNameToResource = new HashMap<String, String>();

	public Link(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
		context = parent;
		try {
			TermFigure child = (TermFigure)TermInstantiator.instance().instantiate((Compound)term.arg(1), this);
			add(child);
		} catch (ClassCastException e) {
			throw new TermVisualizationException(e);
		}
		fileName = (String)term.arg(2);
		Cursor cursor = new Cursor(context.getCanvas().getDisplay(), SWT.CURSOR_HAND);
		setCursor(cursor);
	}

	@Override
	public void dispose() {
		// Nothing to do...
	}

	@Override
	public void handleClick(MouseEvent me) {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getResourceName()));
        IEditorInput editorInput = new FileEditorInput(file);
        try {
            CedalionEditor editor = (CedalionEditor)context.getWorkbenchPart().getSite().getPage().openEditor(editorInput , Activator.CEDALION_EDITOR_NAME);
            editor.refresh();
        } catch (PartInitException e) {
            MessageDialog.openError(context.getWorkbenchPart().getSite().getShell(), "Failed to open editor", e.getMessage());
            e.printStackTrace();
        } catch (TermVisualizationException e) {
            MessageDialog.openError(context.getWorkbenchPart().getSite().getShell(), "Failed to open editor", e.getMessage());
            e.printStackTrace();
        } catch (TermInstantiationException e) {
            MessageDialog.openError(context.getWorkbenchPart().getSite().getShell(), "Failed to open editor", e.getMessage());
			e.printStackTrace();
		} catch (PrologException e) {
            MessageDialog.openError(context.getWorkbenchPart().getSite().getShell(), "Failed to open editor", e.getMessage());
			e.printStackTrace();
		}
    }

	private String getResourceName() {
		return fileNameToResource.get(fileName);
	}
	
	public static void setFileNameToResourceMapping(String fileName, String resource) {
		fileNameToResource.put(fileName, resource);
	}

}
