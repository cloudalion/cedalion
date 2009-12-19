/*
 * Created on Jan 20, 2006
 */
package net.nansore.cedalion.eclipse;

import java.io.IOException;

import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.figures.FontResize;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
    private int fontSizeOffset = 0;
	private Compound term;
	private TermContext context;
	private FontResize resizer;

    /**
     * @param parent
     * @param style
     */
    public VisualTermWidget(Composite parent, int style) {
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
        
        text = new Text(topPanel, SWT.NONE);
        FormData textFD = new FormData();
        textFD.top = new FormAttachment(0, 0);
        textFD.left = new FormAttachment(0, 0);
        textFD.right = new FormAttachment(refreshBtn);
        text.setLayoutData(textFD);
        text.setEnabled(false);
        
        canvas = new FigureCanvas(this);
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
		panel.removeAll();
		if(resizer != null)
			resizer.dispose();
    	Compound compound = term.getProlog().createCompound("fontResize", term, new Integer(fontSizeOffset));
		resizer = new FontResize(compound, context);
		panel.add(resizer);
	}
    /**
     * @return Returns the text.
     */
    public Text getText() {
        return text;
    }
}
