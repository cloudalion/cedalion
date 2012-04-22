/*
 * Created on Jan 21, 2006
 */
package net.nansore.cedalion.figures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.Notifier;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.cedalion.helpers.FigureNavigator;
import net.nansore.cedalion.helpers.FigureNotFoundException;
import net.nansore.cedalion.helpers.HasPivotOffset;
import net.nansore.cedalion.helpers.PivotHorizontalLayout;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This figure represents a term that needs to be visualized.  It operates on "descriptors", which are terms
 * containing the content or the location of a certain Cedalion code element.  
 * Upon creation, it issues a query to get the visuals associated with that code element.
 * This figure draws focus.  When focused, it draws a rectangle around itself, and captures the CedalionEditor's
 * text box, filling it with a textual representation of the underlying term.  The text box is writable, allowing
 * the user to modify its content.  When Enter is hit, this figure modifies the content of the underlying term to
 * the parsed content of the text-box.
 * Also supported by this figure are code completion and a context menu.  Both getting their content by issuing
 * queries to the Cedalion program. 
 */
public class VisualTerm extends Panel implements TermFigure, TermContext, MouseListener, FocusListener, KeyListener, HasPivotOffset{

    private static final int MAX_MENU_ENTRIES = 100;
	private TermContext context;
    private TermFigure contentFigure;
	private Object path;
	private Compound descriptor;
	private Compound projType;
	private Runnable unreg;
	private List<TermFigure> boundFigures = new ArrayList<TermFigure>();
	private static Map<Object, VisualTerm> pathOwner = new HashMap<Object, VisualTerm>();

    /**
     * Construct a new instance
     * @param term the term containing the descriptor and possibly the visualization mode
     * @param parent the figure in which this figure is placed
     * @throws TermVisualizationException if visualization of the term has failed, e.g., if the query failed.
     * @throws TermInstantiationException if a visualization object could not be instantiated
     */
	public VisualTerm(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException {
    	context = parent;
        
        // Register this object with the content
        context.registerTermFigure(path, this);

        try {
            // The first argument is the descriptor, containing the path and additional information
        	descriptor = (Compound)term.arg(1);
        	if(!descriptor.name().equals("::"))
        		System.err.println("Bad descriptor: " + descriptor.name());
            path = ((Compound)descriptor.arg(1)).arg(1);
            if(term.arity() > 1) {
            	projType = (Compound)term.arg(2);
            } else {
            	projType = Compound.createCompound("cpi#default");
            }

            // Set up the GUI
            setLayoutManager(new FlowLayout());
            setRequestFocusEnabled(true);
            // Create the child figures
            contentFigure = createContentFigure(descriptor);
            add(contentFigure);
            
			unreg = Notifier.instance().register(new Compound("::", path, Compound.createCompound("cpi#path")), new Runnable() {
				
				@Override
				public void run() {
					try {
						updateFigure();
					} catch (TermVisualizationException e) {
						e.printStackTrace();
					} catch (TermInstantiationException e) {
						e.printStackTrace();
					}
				}
			});
        } catch (TermVisualizationException e) {
            e.printStackTrace();
            Label label = new Label("<<<" + e.getMessage() + ">>>");
            label.setForegroundColor(new Color(context.getTextEditor().getDisplay(), 255, 0, 0));
            add(label);
        } catch (ClassCastException e) {
            e.printStackTrace();
            Label label = new Label("<<<" + e.getMessage() + ">>>");
            label.setForegroundColor(new Color(context.getTextEditor().getDisplay(), 128, 128, 0));
            add(label);
        }
        pathOwner.put(path, this);
    }

    private TermFigure createContentFigure(Object path) throws TermVisualizationException, TermInstantiationException {
        // Query for the annotated term's visualization
	    Variable vis = new Variable();
		Compound q = Compound.createCompound("cpi#visualizeDescriptor", path, projType, vis);
	    // If successful, build the GUI
        try {
			Map<Variable, Object> s = PrologProxy.instance().getSolution(q);
		    return (TermFigure) TermInstantiator.instance().instantiate((Compound)s.get(vis), this);
		} catch (PrologException e) {
			throw new TermVisualizationException(e);
		}
    }

    public Text getTextEditor() {
        return context.getTextEditor();
    }

    public void bindFigure(TermFigure figure) {
        figure.addMouseListener(this);
        boundFigures.add(figure);
    }

    public void mousePressed(MouseEvent me) {
        if(me.button == 3) {
        	// Context menu
            try {
				createContextMenu(me);
			} catch (TermInstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                   
        }
        else
        {
        	// Gain focus
	        if(canFocus()) {
	            requestFocus();
	            context.getTextEditor().setEnabled(true);
	            context.getTextEditor().addFocusListener(this);
	            context.getTextEditor().setFocus();
	        } else {
	            context.handleClick(me);
	        }
        }
    }

	private void createContextMenu(MouseEvent me) throws TermInstantiationException {
		System.out.println("Right button click");
		Display display = context.getCanvas().getDisplay();
		Menu menu = new Menu(context.getCanvas().getShell(), SWT.POP_UP);
		try {
			Variable varAction = new Variable("Action");
			Iterator<Map<Variable, Object>> results = PrologProxy.instance().getSolutions(Compound.createCompound("cpi#contextMenuEntry", descriptor, varAction));
			int count = 0;
			while(results.hasNext()) {
				Map<Variable, Object> result = (Map<Variable, Object>)results.next();
				Compound action = (Compound)result.get(varAction);
				TermInstantiator.instance().instantiate(action, menu, context);
				if(count++ > MAX_MENU_ENTRIES) {
					MenuItem errItem = new MenuItem(menu, SWT.NONE);
					errItem.setText("<too many results>");
					break;
				}
			}
		} catch (PrologException e1) {
			e1.printStackTrace();
		}
		Point absLocation = me.getLocation().getCopy();
		translateToAbsolute(absLocation);
		org.eclipse.swt.graphics.Point point = display.map(context.getCanvas(), null, new org.eclipse.swt.graphics.Point(absLocation.x, absLocation.y));
		menu.setLocation(point);
		menu.setVisible(true);
		while (!menu.isDisposed() && menu.isVisible()) {
		    if (!display.readAndDispatch())
		        display.sleep();
		}
	}

    private boolean canFocus() {
    	return canModify();
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseDoubleClicked(MouseEvent me) {
        performDefaultAction();
    }

    public void performDefaultAction() {
    	context.performDefaultAction();
    }


    private String termToText() throws IOException, PrologException, TermInstantiationException, ExecutionContextException {
		ExecutionContext exe = new ExecutionContext();
    	return (String) exe.evaluate(Compound.createCompound("cpi#termAsString", path, Compound.createCompound("cpi#constExpr", 5)), new Variable());
    }

    public String getPackage() {
		return context.getPackage();
	}

    public void selectionChanged(TermFigure figure) {
        context.selectionChanged(figure);
    }

    public void registerTermFigure(Object termID, TermFigure figure) {
        context.registerTermFigure(termID, figure);
    }

    public void updateFigure() throws TermVisualizationException, TermInstantiationException {
		contentFigure.dispose();
		if(contentFigure != null) {
			contentFigure.erase();
		    remove(contentFigure);			
		}
        contentFigure = createContentFigure(descriptor);
        add(contentFigure);
        requestFocus();
        setFocus();
        context.figureUpdated();
		navigator().refresh();
    }

	public void setFocus() {
		context.setFocus();
	}

    public Color getColor() {
        return context.getColor();
    }
    
    public Font getFont(int fontType) {
        return context.getFont(fontType);
    }

    public void dispose() {
    	for(TermFigure figure : boundFigures) {
    		figure.removeMouseListener(this);
    	}
		unregisterTermFigure(path, this);
		unreg.run();
        contentFigure.dispose();
        pathOwner.remove(path);
    }

    public void figureUpdated() {
        context.figureUpdated();
    }

    public void unregisterTermFigure(Object termID, TermFigure figure) {
        context.unregisterTermFigure(termID, figure);
    }

	public String getResource() {
		return context.getResource();
	}

    public void focusGained(org.eclipse.swt.events.FocusEvent event) {
        VisualTerm previousFocused = context.getFocused();
        if(previousFocused != null)
        	previousFocused.lostFocus();
        context.setFocused(this);

        LineBorder focusBorder = new LineBorder();
        focusBorder.setStyle(Graphics.LINE_DASH);
        focusBorder.setColor(getColor());
        focusBorder.setWidth(1);
		setBorder(focusBorder);
        context.selectionChanged(this);
        if(canModify()) {
            try {
                String text = termToText();
				context.getTextEditor().setText(text);
                context.getTextEditor().setEnabled(true);
                context.getTextEditor().setSelection(0, text.length());
                context.getTextEditor().addKeyListener(this);
                context.getTextEditor().setFocus();
                Viewport viewport = ((FigureCanvas)getCanvas()).getViewport();
				Point p = viewport.getViewLocation();
                if(p.y > getLocation().y) {
                	viewport.setViewLocation(new Point(p.x, getLocation().y));
                } else if(p.y + viewport.getSize().height < getLocation().y + getSize().height &&
                		viewport.getSize().height > getSize().height) {
                	viewport.setViewLocation(new Point(p.x, getLocation().y + getSize().height - viewport.getSize().height));
                }
                if(p.x > getLocation().x) {
                	viewport.setViewLocation(new Point(getLocation().x, p.y));
                } else if(p.x + viewport.getSize().width < getLocation().x + getSize().width &&
                		viewport.getSize().width > getSize().width) {
                	viewport.setViewLocation(new Point(getLocation().x + getSize().width - viewport.getSize().width, p.y));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (PrologException e) {
				e.printStackTrace();
			} catch (TermInstantiationException e) {
				e.printStackTrace();
			} catch (ExecutionContextException e) {
				e.printStackTrace();
			}
        }
    }

    public void lostFocus() {
        setBorder(null);
        context.getTextEditor().setText("");
        context.getTextEditor().setEnabled(false);
        context.getTextEditor().removeFocusListener(this);
        context.getTextEditor().removeKeyListener(this);
        context.setFocused(null);
	}

	private boolean canModify() {
        try {
			ExecutionContext exe = new ExecutionContext();
        	return exe.isProcDefined(Compound.createCompound("cpi#edit", path, new Variable(), new Variable()));
        } catch (PrologException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void focusLost(org.eclipse.swt.events.FocusEvent arg0) {
    	lostFocus();
    }

    public void keyPressed(KeyEvent event) {
    	if(context.getFocused() != this)
    		return;
    	applyShortcut(event.keyCode, event.stateMask);
        if(event.character == '\r' && event.stateMask == 0) {
            replaceContent();
        } else if(event.stateMask == (SWT.ALT | SWT.SHIFT)) {
        	FigureNavigator nav = navigator();
        	try {
				VisualTerm other = null;
				switch(event.keyCode) {
				case SWT.ARROW_UP:
					other = (VisualTerm)nav.getLastSmallestDecscendant(nav.getFirstDescendantOfPrevSibling(this, VerticalFlow.class, VisualTerm.class), VisualTerm.class);
					break;
				case SWT.ARROW_DOWN:
					other = (VisualTerm)nav.getFirstSmallestDecscendant(nav.getFirstDescendantOfNextSibling(this, VerticalFlow.class, VisualTerm.class), VisualTerm.class);
					break;
				case SWT.ARROW_LEFT:
					other = (VisualTerm)nav.getLastSmallestDecscendant(nav.getFirstDescendantOfPrevSibling(this, HorizontalFlow.class, VisualTerm.class), VisualTerm.class);
					break;
				case SWT.ARROW_RIGHT:
					other = (VisualTerm)nav.getFirstSmallestDecscendant(nav.getFirstDescendantOfNextSibling(this, HorizontalFlow.class, VisualTerm.class), VisualTerm.class);
					break;
				case SWT.PAGE_UP:
					other = (VisualTerm)nav.getAncestor(getParent(), VisualTerm.class);
					break;
				case SWT.PAGE_DOWN:
				case SWT.HOME:
					other = (VisualTerm)nav.getFirstDescendant(this, VisualTerm.class, false);
					break;
				case SWT.END:
					other = (VisualTerm)nav.getLastDescendant(this, VisualTerm.class);
					break;
				}
				if(other != null)
					other.focusGained(null);
			} catch (FigureNotFoundException e) {
				// No navigation...
			}
        }
    }

	private void applyShortcut(int keyCode, int stateMask) {
		String key = keyDescription(keyCode, stateMask);
		
		// Query the command, if exists.
		Variable procVar = new Variable();
		Compound query = new Compound("cpi#shortcutKey", descriptor, key, procVar);
		try {
			Iterator<Map<Variable, Object>> solutions = PrologProxy.instance().getSolutions(query);
			while(solutions.hasNext()) {
				ExecutionContext exe = new ExecutionContext();
				exe.runProcedure((Compound)solutions.next().get(procVar));
			}
		} catch (PrologException e) {
			e.printStackTrace();
		} catch (TermInstantiationException e) {
			e.printStackTrace();
		} catch (ExecutionContextException e) {
			e.printStackTrace();
		}
	}

	private String keyDescription(int keyCode, int stateMask) {
		// Build the key string
		StringBuffer buff = new StringBuffer();
		if((stateMask & SWT.CTRL) != 0) {
			buff.append("Ctrl+");
		}
		if((stateMask & SWT.SHIFT) != 0) {
			buff.append("Shift+");
		}
		if((stateMask & SWT.ALT) != 0) {
			buff.append("Alt+");
		}
		switch((int)keyCode) {
		case SWT.INSERT:
			buff.append("Ins");
			break;
		case SWT.DEL:
			buff.append("Del");
			break;
		case SWT.HOME:
			buff.append("Home");
			break;
		case SWT.END:
			buff.append("End");
			break;
		case SWT.PAGE_UP:
			buff.append("PgUp");
			break;
		case SWT.PAGE_DOWN:
			buff.append("PgDown");
			break;
		case SWT.ESC:
			buff.append("Esc");
			break;
		case SWT.TAB:
			buff.append("Tab");
			break;
		case SWT.F1:
		case SWT.F2:
		case SWT.F3:
		case SWT.F4:
		case SWT.F5:
		case SWT.F6:
		case SWT.F7:
		case SWT.F8:
		case SWT.F9:
		case SWT.F10:
		case SWT.F11:
		case SWT.F12:
			buff.append("F");
			buff.append(keyCode - SWT.F1 + 1);
			break;
		default:
			buff.append(Character.toUpperCase((char)keyCode));
		}
		String key = buff.toString();
		return key;
	}

	private void replaceContent() {
		try {
		    setContentFromString(context.getTextEditor().getText());
		} catch (TermVisualizationException e) {
		    ErrorDialog.openError(context.getTextEditor().getShell(), "Error Updating Element", "The following error has occured: " + e.getMessage(), Status.OK_STATUS);
		} catch (PrologException e) {
			e.printStackTrace();
		} catch (TermInstantiationException e) {
			e.printStackTrace();
		} catch (ExecutionContextException e) {
			e.printStackTrace();
		}
	}

	private FigureNavigator navigator() {
		Control canvas = getCanvas();
		if(canvas instanceof FigureCanvas) {
			return FigureNavigator.getNavigatorForRoot(((FigureCanvas)canvas).getContents());
		} else {
			throw new RuntimeException("Bad Canvas"); 
		}
	}

    private void setContentFromString(String text) throws TermVisualizationException, PrologException, TermInstantiationException, ExecutionContextException {
    	if(text.startsWith("\"")) {
    		text = "!('" + text.substring(1) + "')";
    	}
		ExecutionContext exe = new ExecutionContext();
		exe.runProcedure(Compound.createCompound("cpi#editFromString", path, Compound.createCompound("cpi#constExpr", text)));
        figureUpdated();
        // Schedule a re-focusing of this object
        getCanvas().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				VisualTerm newVT = pathOwner.get(path);
				if(newVT != null)
					newVT.focusGained(null);
			}
		});
    }

    public void keyReleased(KeyEvent arg0) {
    }

    public void handleClick(MouseEvent me) {
        mousePressed(me);
    }

    public Control getCanvas() {
        return context.getCanvas();
    }

	public IWorkbenchPart getWorkbenchPart() {
		return context.getWorkbenchPart();
	}

	/**
	 * Returns a list of proposals for auto-completion.  Each proposal contains a string with
	 * the textual representation of the proposal, and an alias by which this proposal is
	 * to be selected
	 * @param substring the string to match the beginning of the alias to be matched
	 * @param pos ignored
	 * @return an array of proposals
	 */
	public IContentProposal[] getProposals(String substring, int pos) {
	    List<IContentProposal> proposals = new ArrayList<IContentProposal>();
	    try {
			Variable varCompletion = new Variable();
			Variable varAlias = new Variable();
			Iterator<Map<Variable, Object>> solutions = PrologProxy.instance().getSolutions(Compound.createCompound("cpi#autocomplete", descriptor, substring, varCompletion, varAlias));
			while(solutions.hasNext()) {
				Map<Variable, Object> solution = solutions.next();
				final String completion = (String)solution.get(varCompletion);
				final String alias = (String)solution.get(varAlias);
				proposals.add(new IContentProposal() {
	
					public String getContent() {
						return completion;
					}
	
					public int getCursorPosition() {
						int pos;
						for(pos = 0; pos < completion.length(); pos++) {
							if(completion.charAt(pos) == '(')
								return pos + 1;
						}
						return pos;
					}
	
					public String getDescription() {
						return null;
					}
	
					public String getLabel() {
						return alias + "\t[" + completion + "]";
					}});
			}
			
		} catch (PrologException e) {
			e.printStackTrace();
		}
		return proposals.toArray(new IContentProposal[] {}); 
	}

	/*private String toLocalString(String string) {
		String name = string;
		String args = "";
		if(string.contains("(")) {
			name = string.substring(0, string.indexOf("("));
			args = string.substring(string.indexOf("("));
		}
		if(name.contains("#")) {
			name = "'" + name.substring(0, name.indexOf("#")) + "':'" + name.substring(name.indexOf("#") + 1) + "'";
		}
		return name + args;
	}*/

	@Override
	public Image getImage(String imageName) throws IOException {
		return context.getImage(imageName);
	}

	@Override
	public Compound getPath() {
		return (Compound)path;
	}

	@Override
	public VisualTerm getFocused() {
		return context.getFocused();
	}

	@Override
	public void setFocused(VisualTerm visualTerm) {
		context.setFocused(visualTerm);
	}

	@Override
	public int getPivotOffset() {
		return PivotHorizontalLayout.getChildPivotOffset(contentFigure);
	}
}
