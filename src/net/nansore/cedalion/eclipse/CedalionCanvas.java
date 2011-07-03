package net.nansore.cedalion.eclipse;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * This is a SWT widget that holds Cedalion code.
 */
public class CedalionCanvas extends FigureCanvas {

	/**
	 * A lightweight system that forces font anti-aliasing in all modes
	 */
	static public class MyLightweightSystem extends LightweightSystem {

		@Override
		public void setControl(Canvas c) {
			c.addListener(SWT.Paint, new Listener() {
				
				@Override
				public void handleEvent(Event event) {
					event.gc.setTextAntialias(SWT.ON);
				}
			});
			super.setControl(c);
		}
		
	}

	/**
	 * Creates a new canvas, using MyLightweightSystem as the lightweight system
	 * @param parent
	 */
	public CedalionCanvas(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED, new MyLightweightSystem());
	}

}
