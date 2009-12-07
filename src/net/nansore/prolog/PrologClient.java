/*
 * Created on Aug 11, 2006
 */
package net.nansore.prolog;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class PrologClient {

    private static PrologProxy proxy;

    public static Map<Variable, Object> getSolution(Compound q) throws PrologException {
        Iterator<Map<Variable, Object> > i = proxy.getSolutions(new Compound("once", q));
        if(i.hasNext())
            return i.next();
        else
            return null;
    }

    public static Iterator<Map<Variable, Object> > getSolutions(Compound q) throws PrologException {
        return proxy.getSolutions(q);
    }

    public static boolean hasSolution(Compound q) throws PrologException {
        Iterator<Map<Variable, Object> > i = proxy.getSolutions(new Compound("once", q));
        if(i.hasNext()) {
            i.next();
            return true;
        }
        else
            return false;
    }

    public static void initialize(File file) throws IOException {
        proxy = new PrologProxy(file);
        
    }

    public static void terminate() throws IOException {
        proxy.terminate();
        
    }

	public static String toLocalName(String string) {
		int index = string.indexOf("$");
		return string.substring(index + 1);
	}

}
