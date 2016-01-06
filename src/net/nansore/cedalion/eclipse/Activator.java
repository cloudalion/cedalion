package net.nansore.cedalion.eclipse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.nansore.cedalion.figures.Link;
import net.nansore.prolog.Compound;
import net.nansore.prolog.NoSolutionsException;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/**
	 *  The plug-in ID
	 */
	public static final String PLUGIN_ID = "net.nansore.cedalion";

	public static final String CEDALION_EDITOR_NAME = "net.nansore.cedalion.editor";

	/**
	 *  The shared instance
	 */
	private static Activator plugin;

	/** 
	 * Resource bundle.
	 */
	private ResourceBundle resourceBundle;
    private BundleContext context;
    
//    private PackageLoader pkgLoader;

	private IViewOpener viewOpener;

	private TermContext currContext; 
	
	/**
	 * The constructor.
	 */
	public Activator() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
        try {
    		super.start(context);
            this.context = context;

        	String plInterpreter = getPreferenceStore().getString("prologInterpreter");
        	if(plInterpreter == null || plInterpreter.equals("")) {
        		plInterpreter = "pl";
        	}
            try {
            	PrologProxy.initialize(plInterpreter, loadToFileSystem(context, "service.pl"));

				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				loadInitialImage(getPreferenceStore().getString("initialImage"));
	            loadResources(root);
	            
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch(Exception e) {
            e.printStackTrace();
            //throw e;
        }
	}

	private void loadInitialImage(String imageFile) throws NoSolutionsException, PrologException {
		if(imageFile == "") return;
		PrologProxy.instance().getSolution(new Compound("loadImage", imageFile));
	}

	/**
	 * Load a plugin resource into a file-system file.
	 */
	private File loadToFileSystem(BundleContext context, String fileName) throws IOException, FileNotFoundException, PrologException {
		File tmpFile = File.createTempFile("tmp", ".ced");
		URL prologFileURL = context.getBundle().getEntry(fileName);
		URLConnection connection = prologFileURL.openConnection();
		InputStream input = connection.getInputStream();
		FileOutputStream output = new FileOutputStream(tmpFile);
		byte[] data = new byte[1024]; 
		while(true) {
		    int numBytes = input.read(data);
		    if(numBytes < 0)
		        break;
			output.write(data, 0, numBytes);
		}
		output.close();
//		Compound q = new Compound("consult", tmpFile.getAbsolutePath());
//		System.out.println( "consult of " + tmpFile.getAbsolutePath() + (PrologClient.hasSolution(q) ? " succeeded" : " failed"));
		return tmpFile;
	}

	private void loadResources(IContainer container) throws CoreException {
		// Block getting into a project named "cedalion" to avoid duplicate definitions
		if(container instanceof IProject && container.getName().equals("cedalion"))
			return;
		IResource[] members = container.members(true);
		for(int i = 0; i < members.length; i++) {
			String ext = members[i].getFileExtension();
			if(ext != null && ext.equals("ced")) {
				loadResource(members[i]);
			}
			if(members[i] instanceof IContainer) {
                if(!(members[i] instanceof IProject && !((IProject)members[i]).isOpen()))
                    loadResources((IContainer)members[i]);
			}
		}
	}

	/**
	 * Load a workspace file into Cedalion
	 * @param resource the file as a workspace resource
	 */
	public void loadResource(IResource resource) {
		String resourcePath = resource.getFullPath().toString();
		System.out.println("Loading: " + resourcePath);
		String filePath = resource.getLocation().toString();
		String pkg = resource.getParent().getFullPath().toString();
		System.out.println("Package: " + pkg);
		Link.setFileNameToResourceMapping(filePath, resourcePath);
		try {
			PrologProxy.instance().getSolution(new Compound("loadFile", filePath, pkg));
		} catch (PrologException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
        stopPServer();
		super.stop(context);
	}

	private void stopPServer() throws IOException {
        PrologProxy.instance().terminate();
    }

    /**
	 * Returns the shared instance.
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = Activator.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

    /**
     * @return the context
     */
    public BundleContext getContext() {
        return context;
    }

    /**
     * @return The "zoom in" button image
     * @throws IOException
     * @throws CoreException
     */
    public Image getZoomInImage(Display display) throws IOException  {
		URL prologFileURL = context.getBundle().getEntry("icons/increase.gif");
		URLConnection connection = prologFileURL.openConnection();
		InputStream input = connection.getInputStream();
        return new Image(display, input);
    }

    /**
     * @return The "zoom out" button image
     * @throws IOException
     * @throws CoreException
     */
    public Image getZoomOutImage(Display display) throws IOException {
		URL prologFileURL = context.getBundle().getEntry("icons/decrease.gif");
		URLConnection connection = prologFileURL.openConnection();
		InputStream input = connection.getInputStream();
        return new Image(display, input);
    }

    /**
     * @return The "refresh" button image
     * @throws IOException
     * @throws CoreException
     */
    public Image getRefreshImage(Display display) throws IOException {
		URL prologFileURL = context.getBundle().getEntry("icons/refresh.gif");
		URLConnection connection = prologFileURL.openConnection();
		InputStream input = connection.getInputStream();
        return new Image(display, input);
    }

	/**
	 * @return reference to the PrologProxy instance, holding the connection to the Prolog back-end. 
	 */
    public static PrologProxy getProlog() {
		return PrologProxy.instance();
	}
	
	/**
	 * Allows other parties (e.g., the Cedalion editor) to register themselves as the "view opener", used by openView()
	 * @param opener the designated view opener.
	 */
    public void registerViewOpener(IViewOpener opener) {
		viewOpener = opener;
	}
	
	/**
	 * Allo other parties to register themselves as the term context for newly opened views.
	 * @param currContext the designated context
	 */
    public void registerCurrentContext(TermContext currContext) {
		this.currContext = currContext;
	}
	
	/**
	 * Opens a view (using the registered view opener)
	 * @return the new view
	 * @throws PartInitException if construction has failed.
	 */
    public CedalionView openView() throws PartInitException {
		return viewOpener.openView();
	}
	
    /**
     * @return the current context.
     */
	public TermContext currentContext() {
		return currContext;
	}

	/**
	 * Returns an image from the plug-in
	 * @param imageName The name of the image
	 * @param display A display object associated with the GUI
	 * @return the created image
	 * @throws IOException if creation failed
	 */
	public Image getImage(String imageName, Display display) throws IOException {
		URL prologFileURL = context.getBundle().getEntry("icons/" + imageName + ".gif");
		URLConnection connection = prologFileURL.openConnection();
		InputStream input = connection.getInputStream();
        return new Image(display, input);
	}

	/**
	 * Returns the "Take screenshot" button image
	 * @param display A display object associated with the GUI
	 * @return the newly created image
	 * @throws IOException if something goes wrong
	 */
	public Image getScreenshotImage(Display display) throws IOException {
		URL prologFileURL = context.getBundle().getEntry("icons/screenshot16.png"); // Replace with screenshot icon
		URLConnection connection = prologFileURL.openConnection();
		InputStream input = connection.getInputStream();
        return new Image(display, input);
	}
	
}
