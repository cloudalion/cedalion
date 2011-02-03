/*
 * Created on Jan 20, 2006
 */
package net.nansore.cedalion.eclipse;

import java.io.FileOutputStream;
import java.io.IOException;

import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.figures.FontResize;
import net.nansore.cedalion.figures.LabelFigure;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

/**
 * This class is a SWT composite control that contains a text field and a draw2d canvas. 
 * @author boaz
 */
public class VisualTermWidget extends Composite {
    
    private Text text;
    private FigureCanvas canvas;
    private Panel panel;
    private Button zoomInBtn;
    private Button zoomOutBtn;
    private Button refreshBtn;
    private Button snippetBtn;
    private int fontSizeOffset = 0;
	private Compound term;
	private TermContext context;
	private FontResize resizer;

    /**
     * @param parent
     * @param style
     * @param cedalionEditor 
     */
    public VisualTermWidget(Composite parent, int style, final CedalionEditor cedalionEditor) {
        super(parent, style);
        setLayout(new FormLayout());
        
        Composite topPanel = new Composite(this, SWT.NONE);
        topPanel.setLayout(new FormLayout());
        FormData topPanelFD = new FormData();
        topPanelFD.top = new FormAttachment(0,0);
        topPanelFD.left = new FormAttachment(0,0);
        topPanelFD.right = new FormAttachment(100,0);
        topPanel.setLayoutData(topPanelFD);
        
        zoomInBtn = new Button(topPanel, SWT.PUSH);
        try {
            zoomInBtn.setImage(Activator.getDefault().getZoomInImage(getDisplay()));
        } catch (IOException e) {
            zoomInBtn.setText("Zoom In");
        }
        FormData zoomInFD = new FormData();
        zoomInFD.top = new FormAttachment(0,0);
        zoomInFD.right = new FormAttachment(100, 0);
        zoomInBtn.setLayoutData(zoomInFD);
        zoomInBtn.setToolTipText("Increase Font Size");
        zoomInBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				fontSizeOffset += 2;
				try {
					refresh();
				} catch (TermVisualizationException e1) {
					fontSizeOffset -= 2;
				} catch (TermInstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (PrologException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}});
        
        zoomOutBtn = new Button(topPanel, SWT.PUSH);
        try {
            zoomOutBtn.setImage(Activator.getDefault().getZoomOutImage(getDisplay()));
        } catch (IOException e1) {
            zoomOutBtn.setText("Zoom Out");
        }
        FormData zoomOutFD = new FormData();
        zoomOutFD.top = new FormAttachment(0,0);
        zoomOutFD.right = new FormAttachment(zoomInBtn);
        zoomOutBtn.setLayoutData(zoomOutFD);
        zoomOutBtn.setToolTipText("Decrease Font Size");
        zoomOutBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				fontSizeOffset -= 2;
				try {
					refresh();
				} catch (TermVisualizationException e1) {
					fontSizeOffset += 2;
				} catch (TermInstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (PrologException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}});
        
        refreshBtn = new Button(topPanel, SWT.PUSH);
        try {
        	refreshBtn.setImage(Activator.getDefault().getRefreshImage(getDisplay()));
        } catch (IOException e1) {
        	refreshBtn.setText("Zoom Out");
        }
        FormData refreshFD = new FormData();
        refreshFD.top = new FormAttachment(0,0);
        refreshFD.right = new FormAttachment(zoomOutBtn);
        refreshBtn.setLayoutData(refreshFD);
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				try {
					refresh();
				} catch (TermVisualizationException e1) {
					e1.printStackTrace();
				} catch (TermInstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (PrologException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}});
        
        snippetBtn = new Button(topPanel, SWT.PUSH);
        try {
        	snippetBtn.setImage(Activator.getDefault().getScreenshotImage(getDisplay()));
        } catch (IOException e1) {
        	snippetBtn.setText("Zoom Out");
        }
        FormData snippetFD = new FormData();
        snippetFD.top = new FormAttachment(0,0);
        snippetFD.right = new FormAttachment(refreshBtn);
        snippetBtn.setLayoutData(snippetFD);
        snippetBtn.setToolTipText("Take code snippet of the selection");
        snippetBtn.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				takeSnippet(cedalionEditor);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}});
        
        text = new Text(topPanel, SWT.NONE);
        FormData textFD = new FormData();
        textFD.top = new FormAttachment(0, 0);
        textFD.left = new FormAttachment(0, 0);
        textFD.right = new FormAttachment(snippetBtn);
        text.setLayoutData(textFD);
        text.setEnabled(false);
        
        canvas = new CedalionCanvas(this);
        FormData canvasFD = new FormData();
        canvasFD.top = new FormAttachment(topPanel);
        canvasFD.left = new FormAttachment(0, 0);
        canvasFD.right = new FormAttachment(100, 0);
        canvasFD.bottom = new FormAttachment(100, 0);
        canvas.setLayoutData(canvasFD);
        
        panel = new Panel();
        panel.setLayoutManager(new FlowLayout());
        canvas.setContents(panel);
        canvas.setBackground(new Color(getDisplay(), 255, 255, 255));
    }
    protected void takeSnippet(CedalionEditor cedalionEditor) {
		IFigure contents = cedalionEditor.getLastFocused();
		if(contents == null)
			return;
		FileDialog  dlg = new FileDialog(getShell());
		String fileName = dlg.open();
		if(fileName == null)
			return;
		Dimension size = contents.getSize();
		Image image = new Image(getDisplay(), size.width, size.height);
		GC gc = new GC(image);
		//gc.setTextAntialias(SWT.ON);
		Graphics graphics = new SWTGraphics(gc);
		graphics.translate(-contents.getBounds().x, -contents.getBounds().y);
		contents.paint(graphics);
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] {image.getImageData()};
		try {
			FileOutputStream stream = new FileOutputStream(fileName);
			loader.save(stream, SWT.IMAGE_PNG);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		image.dispose();
		
	}
	/**
     * @return Returns the canvas.
     */
    public FigureCanvas getCanvas() {
        return canvas;
    }
    
    public void setTerm(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
    	this.term = term;
    	this.context = context;
    	refresh();
    }
	public void refresh() throws TermVisualizationException, TermInstantiationException, PrologException {
		System.out.println("Labels before refresh: " + LabelFigure.debugCount);
		panel.removeAll();
		if(resizer != null)
			resizer.dispose();
    	Compound compound = term.getProlog().createCompound("fontResize", term, new Integer(fontSizeOffset));
		resizer = new FontResize(compound, context);
		panel.add(resizer);
		System.out.println("Labels after refresh: " + LabelFigure.debugCount);
		System.gc();
	}
    /**
     * @return Returns the text.
     */
    public Text getText() {
        return text;
    }
	@Override
	public void dispose() {
		resizer.dispose();
		super.dispose();
	}
    
}
