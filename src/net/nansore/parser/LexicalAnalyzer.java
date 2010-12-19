package net.nansore.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

public class LexicalAnalyzer {
	private Set<String> keywords;
	private Reader input;
	private int lookahead;
	private Token nextToken;
	private Location location = new Location();
	private String nextSpecial = "";

	public LexicalAnalyzer(Reader input, Set<String> keywords) throws IOException, ParseException {
		this.keywords = keywords;
		this.input = input;
		nextChar();
		advance();
	}

	private void nextChar() throws IOException {
		lookahead = input.read();
	}
	
	public void advance() throws IOException, ParseException {
		// Read whitespaces
		while(Character.isWhitespace(lookahead))
			lookahead = input.read();
		if(lookahead == -1) {
			nextToken = new Token(Token.Type.EOF, "");
			return;
		}
		// Check the next character
		if(Character.isJavaIdentifierStart(lookahead)) {
			nextToken = readIdentifier();
		} else if(Character.isDigit(lookahead)) {
			nextToken = readNumber();
		} else if(lookahead == '\'' || lookahead == '"') {
			nextToken = readString();
		} else {
			nextToken = readSpecial();
		}
	}

	private Token readSpecial() throws ParseException, IOException {
		if(nextSpecial.isEmpty())
			nextSpecial = readAllSpecial();
		for(int split = nextSpecial.length(); split > 0; split--) {
			String first = nextSpecial.substring(0, split);
			if(keywords.contains(first)) {
				nextSpecial = nextSpecial.substring(split);
				return new Token(Token.Type.SPECIAL, first);
			}
		}
		throw new ParseException(location, "Invalid token: " + nextSpecial);
	}

	private String readAllSpecial() throws IOException {
		StringBuffer buf = new StringBuffer();
		while(!Character.isJavaIdentifierPart(lookahead) &&
				!Character.isDigit(lookahead) &&
				lookahead != '\'' &&
				lookahead != '"' &&
				lookahead != -1) {
			buf.append((char)lookahead);
			nextChar();
		}
		return buf.toString();
	}

	private Token readString() throws IOException, ParseException {
		int terminator = lookahead;
		nextChar();
		StringBuffer buf = new StringBuffer();
		while(lookahead != terminator) {
			if(lookahead == -1)
				throw new ParseException(location, "Unexpected end of file in a string");
			if(lookahead == '\\') {
				nextChar();
				switch(lookahead) {
				case 'n':
					buf.append('\n');
					break;
				case 'r':
					buf.append('\r');
					break;
				case 't':
					buf.append('\t');
					break;
				}
			} else {
				buf.append(lookahead);				
			}
			nextChar();
		}
		nextChar();
		return new Token(Token.Type.STRING, buf.toString());
	}

	private Token readNumber() throws IOException, ParseException {
		StringBuffer buf = new StringBuffer();
		while(Character.isDigit(lookahead)) {
			buf.append((char)lookahead);
			nextChar();
		}
		if(lookahead == '.') {
			buf.append((char)lookahead);
			nextChar();
			while(Character.isDigit(lookahead)) {
				buf.append((char)lookahead);
				nextChar();
			}
		}
		if(lookahead == 'e' || lookahead == 'E') {
			buf.append((char)lookahead);
			nextChar();
			if(lookahead != '+' && lookahead != '-')
				throw new ParseException(location, "Expecting + or - after 'e' or 'E' in number");
			buf.append((char)lookahead);
			nextChar();
			while(Character.isDigit(lookahead)) {
				buf.append((char)lookahead);
				nextChar();
			}
		}
		return new Token(Token.Type.NUMBER, buf.toString());
	}

	private Token readIdentifier() throws IOException {
		StringBuffer buf = new StringBuffer();
		while(Character.isJavaIdentifierPart(lookahead)) {
			buf.append((char)lookahead);
			nextChar();
		}
		return new Token(Token.Type.ID, buf.toString());
	}

	Token nextToken() {
		return nextToken;
	}
}
