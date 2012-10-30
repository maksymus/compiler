package compiler;
import org.apache.commons.lang.StringUtils;



public class Lexer {
	public enum Token {
		NUM, 
		ID, 
		IF("if"), 
		ELSE("else"), 
		WHILE("while"), 
		DO("do"), 
		LBRA("{"), 
		RBRA("}"), 
		LPAR("("), 
		RPAR(")"), 
		PLUS("+"), 
		MINUS("-"), 
		LESS("<"), 
		EQUAL("="), 
		SEMICOLON(";"), 
		EOF;
		
		private Object symbol;

		private Token() {}
		
		private Token(String symbol) {
			this.symbol = symbol;
		}
		
		private <T> Token setSymbol(T symbol) {
			this.symbol = symbol;
			return this;
		} 
		
		@SuppressWarnings("unchecked")
		public <T> T getSymbol() {
			return (T) symbol;
		}
		
	    public static Token fromValue(String v) {
	        for (Token c: Token.values()) {
	        	if(c.symbol == null) continue;
	        	
	            if (c.symbol.equals(v)) {
	                return c;
	            }
	        }
	        
	        return null;
	    }
	}
	
	private String string;
	private int pos;
	private Token currentToken;
	
	public Lexer(String string) {
		this.string = string;
	}
	
	public Token getNextToken() {
		return (currentToken = getToken());
	}
	
	private Token getToken() {
		Token token = null; 
		
		while(token == null) {
			String ch = popChar();
			token = Token.fromValue(ch);
			
			if (ch == null) return Token.EOF;
			else if (StringUtils.isWhitespace(ch));
			else if (token != null) return token;
			else if (StringUtils.isNumeric(ch)) {
				int val = Integer.parseInt(ch);
				
				while((ch = topChar()) != null && StringUtils.isNumeric(ch)) {
					val = val * 10 + Integer.parseInt(popChar());
				}
				
				return Token.NUM.setSymbol(val);
			} else if (StringUtils.isAlpha(ch)) {
				String ident = ch;
				
				while((ch = topChar()) != null && StringUtils.isAlpha(ch)) {
					ident += popChar();
				}
				
				if ((token = Token.fromValue(ident)) != null) return token;
				else if (ident.length() == 1) return Token.ID.setSymbol(ident);
				else {
					throw new RuntimeException("Unknown identifier: " + ident);
				}
			} else {
				throw new RuntimeException("Unknown token: " + ch);
			}
		}
		
		return token;
	} 
	
	public Token getCurrentToken() {
		return currentToken;
	}

	private String topChar() {
		return pos < string.length() ? String.valueOf(string.charAt(pos)) : null;
	}
	
	private String popChar() {
		return pos < string.length() ? String.valueOf(string.charAt(pos++)) : null;
	}
	
	public static void main(String[] args) {
		Lexer lexer = new Lexer("a = 5; b = 3; c = a + b;");
		Token token = null;
		
		while((token = lexer.getNextToken()) != Token.EOF) {
			if (token == Token.NUM) {
				System.out.println(token.getSymbol());
			} else if(token == Token.ID) {
				System.out.println(token.getSymbol());
			} else {
				System.out.println(token);
			}
		}
	}
}
