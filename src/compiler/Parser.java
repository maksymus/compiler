package compiler;

import compiler.Lexer.Token;


public class Parser {

	public enum Lexeme {
		VAR, CONST, ADD, SUB, LT, SET, IF1, IF2, WHILE, DO, EMPTY, SEQ, EXPR, PROG
	}
	
	public static class Node {

		private Lexeme lexeme;
		private Object value;
		private Node operation1;
		private Node operation2;
		private Node operation3;
		
		public Node(Lexeme lexeme, Node operation1) {
			this.lexeme = lexeme;
			this.operation1 = operation1;
		}
		
		public <T> Node(Lexeme lexeme, T value) {
			this.lexeme = lexeme;
			this.value = value;
		}

		public Node(Lexeme lexeme, Node operation1, Node operation2) {
			this.lexeme = lexeme;
			this.operation1 = operation1;
			this.operation2 = operation2;
		}

		public Node(Lexeme lexeme, Node operation1, Node operation2, Node operation3) {
			this.lexeme = lexeme;
			this.operation1 = operation1;
			this.operation2 = operation2;
			this.operation3 = operation3;
		}
		
		public Node(Lexeme lexeme) {
			this.lexeme = lexeme;
		}

		public Lexeme getLexeme() {
			return lexeme;
		}
		
		public Node getOperation1() {
			return operation1;
		}
		
		public Node getOperation2() {
			return operation2;
		}
		
		public Node getOperation3() {
			return operation3;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getValue() {
			return (T) value;
		}
		
		@Override
		public String toString() {
			return lexeme.toString() + (value != null ? ": " + value : "") +
					(operation1 != null ? "\n" + operation1 : "") + 
					(operation2 != null ? "\n" + operation2 : "") + 
					(operation3 != null ? "\n" + operation3 : "");
		}
	}
	
	private Lexer lexer;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	public Node parse() {
		Node node = new Node(Lexeme.PROG, statement(lexer.getNextToken()));
		
		Lexer.Token token = lexer.getNextToken();
		
		if (token != Lexer.Token.EOF) {
			throw new RuntimeException("Invalid statement syntax");
		}
		
		return node;
	}

	private Node term(Lexer.Token token) {
		Node node = null;
		
		if (token == Lexer.Token.ID) {
			node = new Node(Lexeme.VAR, token.getSymbol());
		} else if (token == Lexer.Token.NUM) {
			node = new Node(Lexeme.CONST, token.getSymbol());
		} else /* parent expr */ {
			node = parentesisExpr(token);
		}
		
		return node;
	} 
	
	private Node sum(Lexer.Token token) {
		Node node = term(token);
		
		while ((token = lexer.getNextToken()) == Lexer.Token.PLUS || token == Lexer.Token.MINUS) {
			if (token == Lexer.Token.PLUS) {
				node = new Node(Lexeme.ADD, node, term(lexer.getNextToken()));
			} else {
				node = new Node(Lexeme.SUB, node, term(lexer.getNextToken()));
			}
		}
		
		return node;
	}
	
	private Node test(Lexer.Token token) {
		Node node = sum(token);
		
		if(lexer.getCurrentToken() == Token.LESS) {
			node = new Node(Lexeme.LT, node, sum(lexer.getNextToken()));
		}
		
		return node;
	}
	
	private Node expr(Lexer.Token token) {
		
		if(token != Lexer.Token.ID) {
			return test(token);
		}
		
		Node node = test(token);
		
		if(node.lexeme == Lexeme.VAR && lexer.getCurrentToken() == Token.EQUAL) {
			node = new Node(Lexeme.SET, node, expr(lexer.getNextToken()));
		}
		
		return node;
	}
	
	private Node parentesisExpr(Lexer.Token token) {
		if (Token.LPAR != token) {
			throw new RuntimeException(" '(' expected");
		}
		
		Node node = expr(lexer.getNextToken());
		
		if (Token.RPAR != lexer.getCurrentToken()) {
			throw new RuntimeException(" ')' expected");
		}
		
		return node;
	}
	
	private Node statement(Lexer.Token token) {
		Node node = null;
		
		if (token == Lexer.Token.IF) {
			Node parentesisExpr = parentesisExpr(lexer.getNextToken());
			Node statement1 = statement(lexer.getNextToken());
			
			if (lexer.getNextToken() == Lexer.Token.ELSE) {
				node = new Node(Lexeme.IF2, parentesisExpr, statement1, statement(lexer.getNextToken()));
			} else {
				node = new Node(Lexeme.IF1, parentesisExpr, statement1);
			}
			
		} else if (token == Lexer.Token.WHILE) {
			Node parentesisExpr = parentesisExpr(lexer.getNextToken());
			node = new Node(Lexeme.WHILE, parentesisExpr, statement(lexer.getNextToken()));
		} else if (token == Lexer.Token.DO) {
			
		} else if (token == Lexer.Token.LBRA) {
			node = new Node(Lexeme.EMPTY);
			
			while ((token = lexer.getNextToken()) != Token.RBRA) {
				node = new Node(Lexeme.SEQ, node, statement(token));
				if(Token.RBRA == lexer.getCurrentToken()) break;
			}
			
		} else if (token == Lexer.Token.SEMICOLON) {
			node = new Node(Lexeme.EMPTY);
		} else /* expression */ {
			node = new Node(Lexeme.EXPR, expr(token));
			
			if (lexer.getCurrentToken() != Lexer.Token.SEMICOLON) {
				throw new RuntimeException(" ';' expected");
			}
		}
		
		return node;
	}
	
	public static void main(String[] args) {
		String string = "{ a = 3; if (0 < a)  { a = a - 1; } }";
//		String string = "{ a = 10; while (0 < a) { b = b + 1; } }";
//		String string = "{ a = 5; b = 3; c = 4; if (a < 5) { d = a + b - c; } else { d = a - b - c;} }";
//		String string = "{ if (a < 5) { d = a + b - c; } else { d = a - b - c; } }";
		
		Parser.Node ast = new Parser(new Lexer(string)).parse();
		System.out.println(ast);
	}
}
