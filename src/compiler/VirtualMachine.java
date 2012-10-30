package compiler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * 
 * FETCH x - положить на стек значение переменной x
 * STORE x - сохранить в переменной x значение с вершины стека
 * PUSH  n - положить число n на вершину стека
 * POP     - удалить число с вершины стека
 * ADD     - сложить два числа на вершине стека
 * SUB     - вычесть два числа на вершине стека
 * LT      - сравнить два числа с вершины стека (a < b). Результат - 0 или 1
 * JZ    a - если на вершине стека 0 - перейти к адресу a.
 * JNZ   a - если на вершине стека не 0 - перейти к адресу a.
 * JMP   a - перейти к адресу a
 * HALT    - завершить работу
 */
public class VirtualMachine {
	
	public static class Command <T> {
		public final OpCode opCode;
		public T arg;

		public Command(OpCode opCode) {
			this.opCode = opCode;
			this.arg = null;
		}
		
		public Command(OpCode opCode, T arg) {
			this.opCode = opCode;
			this.arg = arg;
		}
		
		@Override
		public String toString() {
			return opCode + (arg != null ? " " + arg.toString() : "");
		}
	}
	
	@SuppressWarnings("serial")
	public static class Program extends LinkedList<Command<?>> {}
	
	public enum OpCode {
		IFETCH, ISTORE, IPUSH, IPOP, IADD, ISUB, ILT, JZ, JNZ, JMP, HALT
	}

	public void run(Program program) {
		Map<String, Integer> vars = new HashMap<String, Integer>();
 		Stack<Integer> stack = new Stack<Integer>();
		
		for (int pc = 0; true; pc++) {
			Command<?> command = program.get(pc);
			
			OpCode opCode = command.opCode;
			
			if (opCode == OpCode.IFETCH) {
				stack.push(vars.containsKey(command.arg) ? vars.get(command.arg) : 0);
			} else if (opCode == OpCode.ISTORE) {
				vars.put((String) command.arg, stack.peek());
			} else if (opCode == OpCode.IPUSH) {
				stack.push((Integer) command.arg);
			} else if (opCode == OpCode.IPOP) {
				stack.pop();
			} else if (opCode == OpCode.HALT) {
				break;
			} else if (opCode == OpCode.IADD) {
				stack.push(stack.pop() + stack.pop());
			} else if (opCode == OpCode.ISUB) {
				stack.push(stack.pop() - stack.pop());
			} else if (opCode == OpCode.ILT) {
				stack.push(stack.pop() < stack.pop() ? 1 : 0);
			} else if (opCode == OpCode.JZ) {
				if(stack.pop() == 0) pc = ((Integer) command.arg) - 1;
			} else if (opCode == OpCode.JMP) {
				pc = ((Integer) command.arg) - 1;
			}
		}
		
		for (String k : vars.keySet()) {
			System.out.println(k + "=>" + vars.get(k));
		}
	}
	
	public static void main(String[] args) {
//		String string = "{ a = 5; b = 1; c = 100; d = a + b - c; }";
//		String string = "{ a = 1; b = 2; if (a < b) { c = 1; } else { c = 2; } }";
		String string = "{ a = 11; while (0 < a) { b = b + 1; a = a - 1; } }";
		
		Parser.Node ast = new Parser(new Lexer(string)).parse();
		Program p = new Compiler().compile(ast);
		System.out.println(p);
		new VirtualMachine().run(p);
	}
}
