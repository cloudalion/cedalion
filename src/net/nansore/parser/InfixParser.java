package net.nansore.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.nansore.parser.Operator.Assoc;

public class InfixParser {
	private Map<String, Operator> ops = new HashMap<String, Operator>();
	private Set<String> keywords = new HashSet<String>();
	
	public void defineOp(String name, Assoc assoc, boolean prefix, int prec) {
		ops.put(name, new Operator(name, assoc, prefix, prec));
		keywords.add(name);
	}
	
/*	public <T> T parse(Reader input, ParserContext<T> context) throws IOException, ParseException {
		Stack<T> valueStack = new Stack<T>();
		Stack<Operator> opStack = new Stack<Operator>();
		LexicalAnalyzer lex = new LexicalAnalyzer(input, keywords);
		// TODO: Complete this...
		return null;
	}*/

}
