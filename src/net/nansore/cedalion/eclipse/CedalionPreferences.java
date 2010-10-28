package net.nansore.cedalion.eclipse;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class CedalionPreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public CedalionPreferences() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Options for the VisualTerm plugin");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new FontFieldEditor("normalFont", "Normal Font", getFieldEditorParent()));
		addField(new FontFieldEditor("symbolFont", "Symbol Font", getFieldEditorParent()));
		addField(new FileFieldEditor("prologInterpreter", "Prolog Interpreter", getFieldEditorParent()));
		addField(new BooleanFieldEditor("useProxy", "Use Proxy", getFieldEditorParent()));
		addField(new StringFieldEditor("proxyHost", "Proxy host", getFieldEditorParent()));
		addField(new IntegerFieldEditor("proxyPort", "Proxy port", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}