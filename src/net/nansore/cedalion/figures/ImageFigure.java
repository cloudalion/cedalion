package net.nansore.cedalion.figures;

import java.util.HashMap;
import java.util.Map;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class ImageFigure extends Label implements TermFigure {
	
	private static Map<String, Image> images = new HashMap<String, Image>();
	
	public ImageFigure(Compound term, TermContext context) {
		String imageID = term.arg(1).toString();
		try {
			Image img = createImage(context, imageID);
			setIcon(img);
		} catch (CoreException e) {
			setText("Failed to load image: " + imageID);
			e.printStackTrace();
		}
	}

	public static Image createImage(TermContext context, String imageID)
			throws CoreException {
		if(imageID.equals("cpi#none")) {
			return null;
		}
		if(images.containsKey(imageID))
			return images.get(imageID);
		
		IPath filePath = new Path(imageID.replaceFirst("#", "/"));
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
		Image img = new Image(context.getCanvas().getDisplay(), new ImageData(file.getContents()));
		images.put(imageID, img);
		return img;
	}

	@Override
	public void updateFigure() throws TermVisualizationException,
			TermInstantiationException {
	}

	@Override
	public void dispose() {
	}

}
