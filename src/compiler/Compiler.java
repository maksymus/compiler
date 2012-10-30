package compiler;

import static compiler.VirtualMachine.OpCode.HALT;
import static compiler.VirtualMachine.OpCode.IADD;
import static compiler.VirtualMachine.OpCode.IFETCH;
import static compiler.VirtualMachine.OpCode.ILT;
import static compiler.VirtualMachine.OpCode.IPOP;
import static compiler.VirtualMachine.OpCode.IPUSH;
import static compiler.VirtualMachine.OpCode.ISTORE;
import static compiler.VirtualMachine.OpCode.ISUB;

import compiler.VirtualMachine.Command;
import compiler.VirtualMachine.OpCode;
import compiler.VirtualMachine.Program;

public class Compiler {

	private Program program = new Program(); 
	
	public VirtualMachine.Program compile(Parser.Node node) {
		
		switch (node.getLexeme()) {
		case VAR:
			program.add(generate(IFETCH, node.getValue()));
			break;
		case CONST:
			program.add(generate(IPUSH, node.getValue()));
			break;
		case ADD:
			compile(node.getOperation2());
			compile(node.getOperation1());
			program.add(generate(IADD));
			break;
		case DO:
			break;
		case EXPR:
			compile(node.getOperation1());
			program.add(generate(IPOP));
			break;
		case IF1:
			Command<Integer> jz2 = generate(OpCode.JZ, 0);
			compile(node.getOperation1());
			program.add(jz2);
			compile(node.getOperation2());
			jz2.arg = program.size();
			break;
		case IF2:
			Command<Integer> jz = generate(OpCode.JZ, 0);
			Command<Integer> jmp = generate(OpCode.JMP, 0);

			compile(node.getOperation1());
			program.add(jz);
			
			compile(node.getOperation2());
			program.add(jmp);
			jz.arg = program.size();
			
			compile(node.getOperation3());
			jmp.arg = program.size();
			break;
		case LT:
			compile(node.getOperation2());
			compile(node.getOperation1());
			program.add(generate(ILT));
			break;
		case PROG:
			compile(node.getOperation1());
			program.add(generate(HALT));
			break;
		case SEQ:
			compile(node.getOperation1());
			compile(node.getOperation2());
			break;
		case SET:
			compile(node.getOperation2());
			program.add(generate(ISTORE, node.getOperation1().getValue()));
			break;
		case SUB:
			compile(node.getOperation2());
			compile(node.getOperation1());
			program.add(generate(ISUB));
			break;
		case WHILE:
			Command<Integer> jz1 = generate(OpCode.JZ, program.size());
			Command<Integer> jmp1 = generate(OpCode.JMP, program.size());
			
			compile(node.getOperation1());
			program.add(jz1);
			compile(node.getOperation2());
			program.add(jmp1);

			jz1.arg = program.size();
			break;
		default:
			break;
		} 
		
		return program;
	}
	
	private <T> Command<T> generate(OpCode opCode, T value) {
		return new Command<T>(opCode, (T) value);
	}

	private Command<?> generate(OpCode opCode) {
		return new Command<Void>(opCode);
	}

	
	public static void main(String[] args) {
//		String string = "{ a = 5; b = 3; c = 4; d = a + b - c; }";
//		String string = "{ a = 5; b = 3; c = 4; if (a < 5) { d = a + b - c; } else { d = a - b - c;} }";
		String string = "{ a = 10; while (0 < a) { b = b + 1; } }";
		
		Parser.Node ast = new Parser(new Lexer(string)).parse();
		Program p = new Compiler().compile(ast);
		
		System.out.println(p);
	}
}
