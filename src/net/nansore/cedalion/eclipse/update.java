/*
 * Created on Jan 24, 2006
 */
package net.nansore.cedalion.eclipse;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to manage editors
 */
public class update {

    private static List<CedalionEditor> editors = new ArrayList<CedalionEditor>();
    
    /**
     * @param editor
     */
    public static synchronized void registerEditor(CedalionEditor editor) {
        editors.add(editor);
    }
    /**
     * @param editor
     */
    public static synchronized void unregisterEditor(CedalionEditor editor) {
        editors.remove(editor);
    }
}
