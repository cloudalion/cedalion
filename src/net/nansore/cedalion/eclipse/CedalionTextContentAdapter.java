package net.nansore.cedalion.eclipse;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * This class is a part of the auto-completion mechanism.  It is responsible for inserting the selected text.
 * @author boaz
 *
 */
public class CedalionTextContentAdapter extends TextContentAdapter {

	@Override
	public void insertControlContents(Control control, String text,
			int cursorPosition) {
		Point selection = ((Text) control).getSelection();
		((Text) control).setText(text);
		if (cursorPosition < text.length()) {
			((Text) control).setSelection(selection.x + cursorPosition,
					selection.x + cursorPosition);
		}
	}

}
