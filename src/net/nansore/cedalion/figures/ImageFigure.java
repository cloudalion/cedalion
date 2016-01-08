package net.nansore.cedalion.figures;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.NoSolutionsException;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class ImageFigure extends Label implements TermFigure {
	
	private static Map<Compound, Image> images = new HashMap<Compound, Image>();
	
	public ImageFigure(Compound term, TermContext context) {
		 Compound imageID = (Compound)term.arg(1);
		
		try {
			Image img = createImage(context, imageID);
			setIcon(img);
		} catch (CoreException e) {
			setText("Failed to load image: " + imageID);
			e.printStackTrace();
		} catch (IOException e) {
			setText(e.getLocalizedMessage());
			e.printStackTrace();
		}
		context.bindFigure(this);
	}

	public static Image createImage(TermContext context, Compound imageID)
			throws CoreException, IOException {
		if(imageID.name().equals("cpi#none")) {
			return null;
		}
		if(images.containsKey(imageID))
			return images.get(imageID);
		
		Map<Variable, Object> solution;
		Variable varURL = new Variable();
		try {
			solution = PrologProxy.instance().getSolution(new Compound("cpi#imageURL", imageID, varURL));
		} catch (NoSolutionsException e) {
			throw new IOException("URL for image " + imageID + " is not defined");
		} catch (PrologException e) {
			throw new IOException(e);
		}
		URL url = new URL((String) solution.get(varURL));
		URLConnection conn = url.openConnection();
		Image img = new Image(context.getCanvas().getDisplay(), conn.getInputStream());
		images.put(imageID, img);
		return img;
	}

	@Override
	public void updateFigure() throws TermVisualizationException,
			TermInstantiationException {
	}

	@Override
	public void dispose() {
		erase();
	}

}
