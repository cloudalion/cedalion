package net.nansore.cedalion.eclipse;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class CedalionTextContentAdapter extends TextContentAdapter {

	@Override
	public void insertControlContents(Control control, String text,
			int cursorPosition) {
		Point selection = ((Text) control).getSelection();
		((Text) control).setText(text);
		// Insert will leave the cursor at the end of the inserted text. If this
		// is not what we wanted, reset the selection.
		if (cursorPosition < text.length()) {
			((Text) control).setSelection(selection.x + cursorPosition,
					selection.x + cursorPosition);
		}
	}

}
