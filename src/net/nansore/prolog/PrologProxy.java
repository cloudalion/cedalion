/*
 * Created on Aug 10, 2006
 */
package net.nansore.prolog;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class represents the logic engine behind Cedalion.  It is implemented to use SWI-Prolog.
 * Its current implementation assumes all calls are made from a single thread.
 */
public class PrologProxy {
    private Process proc;
    private InputStreamReader input;
    private OutputStreamWriter output;
    private int nextChar = -2;
    private Writer log;
	private static ThreadLocal<PrologProxy> prologTLS = new ThreadLocal<PrologProxy>();
    private static final Pattern intRegex = Pattern.compile("-?[0-9]+");
    private static final Pattern floatRegex = Pattern.compile("-?[0-9]+(\\.[0-9]+)?([eE][+\\-][0-9]+)?");
    
    private PrologProxy(String prologInterpreter, File file) throws IOException {
        proc = Runtime.getRuntime().exec(prologInterpreter + " -s " + file.toString() + " -t qryStart");
        input = new InputStreamReader(proc.getInputStream());
        output = new OutputStreamWriter(proc.getOutputStream());
        log = new OutputStreamWriter(new FileOutputStream("pl.log"));
    }

    private boolean hasMoreSolutions() throws IOException, PrologException {
    	Map<String, Variable> varMap = new HashMap<String, Variable>();
        while(!isAtEOF()) {
            switch(getNextChar()) {
            case '.':
                readNext();
                return false;
            case '-':
                readNext();
                return true;
            case '!':
                readNext();
                throw new PrologException(readTerm(varMap));
            default:
                readNext();
            }
        }
        return false;
    }

    /**
     * Runs a query and returns an iterator on the solutions.
     * @param query the query term.
     * @return an iterator on the solutions.  Each solution is a map from the variables in query to their solution values
     * @throws PrologException if an exception was thrown on the Prolog side.
     */
    public synchronized Iterator<Map<Variable, Object> > getSolutions(Compound query) throws PrologException {
        try {
        	// Prepare a map with the variables in the query
        	Map<String, Variable> queryVars = new HashMap<String, Variable>();
        	for(Variable var : query.variables()) {
        		queryVars.put(var.name(), var);
        	}
            // Write the query
            writeQuery(query);
            // Read all results
            List<Map<Variable, Object> > results = new ArrayList<Map<Variable, Object> >();
            while(hasMoreSolutions()) {
                Map<String, Variable> varMap = new HashMap<String, Variable>();
				Object term = readTerm(varMap );
                
                Map<Variable, Object> result = new HashMap<Variable, Object>();
                while(term instanceof Compound && ((Compound)term).name().equals(".")) {
                    Compound compound = (Compound)term;
                    Compound item = (Compound)compound.arg(1);
                    Variable var = queryVars.get(item.arg(1).toString());
                    if(var == null) {
                    	var = new Variable(item.arg(1).toString());
                    }
                    result.put(var, item.arg(2));
                    term = compound.arg(2);
                }
                results.add(result);
            }
            if(log != null)
                log.flush();
            return results.iterator();
        } catch (IOException e) {
            throw new PrologException(e);
        }
    }
    private void writeQuery(Compound query) throws IOException {
        List<Variable> vars = new ArrayList<Variable>();
        findAllVars(query, vars);
        Object pattern = Compound.createCompound("[]");
        for(Iterator<Variable> i = vars.iterator(); i.hasNext(); ) {
            Variable var = i.next();
            pattern = new Compound(".", new Compound("=", var.name(), var), pattern);
        }
        writeTerm(new Compound("query", pattern, query));
        write(".\r\n");
        output.flush();
//        write("query(ok, halt).\n");
    }
    private Object readTerm(Map<String, Variable> varMap) throws IOException {
        skipWhiteSpace();
        String atom;
        if(getNextChar() == '\'') {
            // A quoted atom
            atom = readQuotedAtom();
        } else if(getNextChar() == '(') {
            // An enclosed term
            readNext();
            Object term = readTerm(varMap);
            skipWhiteSpace();
            if(getNextChar() != ')')
                throw new IOException("Protocol error: expected: ')', found: " + getNextChar());
            readNext();
            return term;
        } else if(isVarBeginning(getNextChar())) {
            // A variable
            return readVar(varMap);
        } else {
            // A normal atom
            atom = readAtom();
        }
        skipWhiteSpace();
        if(getNextChar() == '(') {
            // A compound term
            readNext();
            List<Object> args = new ArrayList<Object>();
            while(true) {
                args.add(readTerm(varMap));
                skipWhiteSpace();
                if(getNextChar() == ')') {
                    readNext();
                    break;
                } else if(getNextChar() != ',') {
                    throw new IOException("Bad protocol: expected ',', found: " + getNextChar());
                }
                readNext();
            }
            if(atom.equals("!") && args.size() == 1) {
            	Object str = args.get(0);
            	if(str instanceof Compound)
            		return ((Compound)str).name();
            	else
            		return str.toString();
            }
            else
            	return new Compound(atom, args.toArray());
        } else {
            // Check for numeric values
        	if(intRegex.matcher(atom).matches()) {
                return Integer.valueOf(atom);
            } else if(floatRegex.matcher(atom).matches()) {
                // Try float
                return Float.valueOf(atom);
            } else {
                return new Compound(atom);
            }
        }
        
    }
    private String readAtom() throws IOException {
        StringBuffer buff = new StringBuffer();
        while(!isSpecial(getNextChar())) {
            buff.append(getNextChar());
            readNext();
        }
        return buff.toString();
    }
    private boolean isSpecial(char c) {
        if(c == '(')
            return true;
        if(c == ')')
            return true;
        if(c == ',')
            return true;
        if(c == '\n')
            return true;
        if(Character.isWhitespace(c))
            return true;
        return false;
    }
    private Object readVar(Map<String, Variable> varMap) throws IOException {
        String varName = readAtom();
        if(varMap.get(varName) != null)
        	return varMap.get(varName);
        else {
    		Variable variable = new Variable(varName);
    		varMap.put(varName, variable);
    		return variable;        	
        }
    }
    private boolean isVarBeginning(char c) {
        if(c == '_')
            return true;
        if(c >= 'A' && c <= 'Z')
            return true;
        return false;
    }
    private String readQuotedAtom() throws IOException {
        StringBuffer buff = new StringBuffer();
        readNext();
        while(getNextChar() != '\'') {
            if(getNextChar() == '\\') {
                readNext();
                switch(getNextChar()) {
                case 'n':
                    buff.append('\n');
                    break;
                case 'r':
                    buff.append('\r');
                    break;
                case 't':
                    buff.append('\t');
                    break;
                case '\'':
                    buff.append('\'');
                    break;
                case '\\':
                    buff.append('\\');
                    break;
                }
            } else
                buff.append(getNextChar());
            readNext();
        }
        readNext();
        return buff.toString();
    }
    private void skipWhiteSpace() throws IOException {
        while(Character.isWhitespace(getNextChar()))
            readNext();
    }
    private void writeTerm(Object term) throws IOException {
        if(term instanceof String)
            writeString((String)term);
        else if(term instanceof Variable) {
        	if(((Variable)term).isBound())
        		writeTerm(((Variable)term).boundTo());
        	else
        		write(((Variable)term).name());
        }
        else if(term instanceof Compound)
            writeCompound((Compound)term);
        else
            write(term.toString());
            
    }
    private void writeString(String str) throws IOException {
    	write("!(");
    	writeAtom(str);
    	write(")");
	}
	private void writeCompound(Compound compound) throws IOException {
        writeAtom(compound.name());
        if(compound.arity() > 0) {
            write("(");
            boolean first = true;
            for(int i = 1; i <= compound.arity(); i++) {
                if(!first)
                    write(",");
                first = false;
                writeTerm(compound.arg(i));
            }
            write(")");
        }
            
        
    }
    private void writeAtom(String string) throws IOException {
        write("'");
        for(int i = 0; i < string.length(); i++) {
            switch(string.charAt(i)) {
            case '\n':
                write("\\n");
                break;
            case '\r':
                write("\\r");
                break;
            case '\t':
                write("\\t");
                break;
            case '\'':
                write("\\'");
                break;
            case '\\':
                write("\\\\");
                break;
            default:
                write(string.charAt(i));
            }
        }
        write("'");
    }
    private void write(char c) throws IOException {
        output.write(c);
        if(log != null)
            log.write(c);
    }
    private void findAllVars(Object term, List<Variable> vars) {
        if(term instanceof Variable)
            vars.add((Variable)term);
        else if(term instanceof Compound) {
            Compound comp = (Compound)term;
            for(int i = 1; i <= comp.arity(); i++) {
                findAllVars(comp.arg(i), vars);
            }
        }
    }
    
    private void readNext() throws IOException {
        nextChar = input.read();
        if(log != null)
            log.write((char)nextChar);
    }
    
    private boolean isAtEOF() {
        return nextChar == -1;
    }
    
    private char getNextChar() throws EOFException {
    	if(nextChar == -1)
    		throw new EOFException("Premature end of file");
        return (char)nextChar;
    }
    
    public static void main(String[] args) throws IOException, PrologException {
    	System.out.println("Hello");
        PrologProxy p = new PrologProxy("c:\\program files\\pl\\bin\\plcon.exe", new File("d:/workspace/cedalion/service.pl"));
        Variable varX = new Variable("X");
        Iterator<Map<Variable, Object>> i = p.getSolutions(new Compound("a", varX));
        while(i.hasNext()) {
            System.out.println("Result : " + i.next().get(varX));
        }
        i = p.getSolutions(new Compound("=", varX, "x"));
        while(i.hasNext()) {
            System.out.println("Result : " + i.next().get(varX));
        }
        i = p.getSolutions(new Compound("a", varX));
        while(i.hasNext()) {
            System.out.println("Result : " + i.next().get(varX));
        }
        p.terminate();
    }
    
    private void write(String s) throws IOException {
        output.write(s);
        if(log != null)
            log.write(s);
    }
    
    /**
     * Close connection to the Prolog
     * @throws IOException is something goes wrong
     */
    public void terminate() throws IOException {
        input.close();
        proc.destroy();
    }
    
    /**
     * Runs a query and returns whether it has a solution
     * @param q the query to run
     * @return true if q has a solution
     * @throws PrologException if an exception was thrown on the Prolog side
     */
    public boolean hasSolution(Compound q) throws PrologException {
        Iterator<Map<Variable, Object> > i = getSolutions(new Compound("once", q));
        if(i.hasNext()) {
            i.next();
            return true;
        }
        else
            return false;
    }

    /**
     * Runs a query and returns its solution
     * @param q the query to run
     * @return a solution, mapping variables to their values
     * @throws PrologException if an exception was thrown on the Prolog side
     * @throws NoSolutionsException if no solutions have been encountered
     */
    public Map<Variable, Object> getSolution(Compound q) throws NoSolutionsException, PrologException {
        Iterator<Map<Variable, Object> > i = getSolutions(new Compound("once", q));
        if(i.hasNext())
            return i.next();
        else
            throw new NoSolutionsException("Query " + q + " has no solutions");
    }
    
	/**
	 * Initialize the logic engine on the running thread
	 * @param prologInterpreter the path to the SWI-Prolog executable
	 * @param file the Prolog file implementing core Cedalion
	 * @throws IOException if something goes wrong
	 */
    public static void initialize(String prologInterpreter, File file) throws IOException {
		prologTLS.set(new PrologProxy(prologInterpreter, file));
	}
	/**
	 * @return an instance of the logic engine associated with the running thread
	 */
	public static PrologProxy instance() {
		return prologTLS.get();
	}
}
