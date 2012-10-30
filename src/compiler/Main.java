package compiler;
/**
 * EBNF:
 * <program> ::= <statement>
 * <statement> ::= 	"if" <paren-expr> <statement> |
 *                 				"if" <paren-expr> <statement> "else" <statement> |
 *                 				"while" <paren-expr> <statement> |
 *                 				"do" <statement> "while" <paren-expr> |
 *                 				"{" { <statement> } "}" |
 *                 				<expr> ";" |
 *                 				";"
 * <paren-expr> ::= "(" <expr> ")"
 * <expr> ::= <test> | <id> "=" <expr>
 * <test> ::= <sum> | <sum> "<" <sum>
 * <sum>  ::= <term> | <sum> "+" <term> | <sum> "-" <term>
 * <term> ::= <id> | <int> | <paren-expr>
 * <id>   ::= "a" | "b" | ... | "z"
 * <int>  ::= <digit>, { <digit> }
 * <digit> ::= "0" | "1" | ... | "9"
 */
public class Main {
	public static void main(String[] args) {
		String string = "{ a = 3; if (0 < a)  { a = a - 2 ; } }";
//		String string = "{ a = 1; b = 2; if (a < b) { c = 1; } else { c = 2; } }";
		
		Parser.Node ast = new Parser(new Lexer(string)).parse();
		
		VirtualMachine.Program program =  new Compiler().compile(ast);
		
		new VirtualMachine().run(program);
	}
}
