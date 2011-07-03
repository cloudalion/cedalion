package net.nansore.cedalion.eclipse;

import org.eclipse.ui.PartInitException;

/**
 * An implementation of this interface is able to open the CedalionView in the Eclipse workbench
 */
public interface IViewOpener {

	/**
	 * Open the CedalionView.
	 * @return the CedalionView instance that has been opened
	 * @throws PartInitException if something goes wrong
	 */
	CedalionView openView() throws PartInitException;

}
