package net.nansore.cedalion.eclipse;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class CedalionViewScreenshot implements IViewActionDelegate {

	private CedalionView view;

	@Override
	public void run(IAction action) {
		FileDialog  dlg = new FileDialog(view.getSite().getShell());
		String fileName = dlg.open();
		if(fileName == null)
			return;
		IFigure contents = ((FigureCanvas)view.getCanvas()).getContents();
		Dimension bounds = contents.getSize();
		Image image = new Image(view.getCanvas().getDisplay(), bounds.width, bounds.height);
		GC gc = new GC(image);
		Graphics graphics = new SWTGraphics(gc);
		contents.paint(graphics);
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] {image.getImageData()};
		try {
			loader.save(new FileOutputStream(fileName), SWT.IMAGE_PNG);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		image.dispose();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void init(IViewPart view) {
		this.view = (CedalionView) view;
	}

}
