package net.nansore.cedalion.eclipse;

import org.eclipse.ui.PartInitException;

public interface IViewOpener {

	CedalionView openView() throws PartInitException;

}
