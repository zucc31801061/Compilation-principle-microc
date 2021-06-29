/* File MicroC/Machine.java
   A unified-stack abstract machine for imperative programs.
   sestoft@itu.dk * 2001-03-21, 2009-09-24

   To execute a program file using this abstract machine, do:

      java Machine <programfile> <arg1> <arg2> ...

   or, to get a trace of the program execution:

      java Machinetrace <programfile> <arg1> <arg2> ...

*/

import java.io.*;
import java.util.*;

class Machine {
  public static void main(String[] args)        
    throws FileNotFoundException, IOException {
    if (args.length == 0) 
      System.out.println("Usage: java Machine <programfile> <arg1> ...\n");
    else
      execute(args, false);
  }

  // These numeric instruction codes must agree with Machine.fs:

  final static int 
    CSTI = 0, ADD = 1, SUB = 2, MUL = 3, DIV = 4, MOD = 5, 
    EQ = 6, LT = 7, NOT = 8, 
    DUP = 9, SWAP = 10, 
    LDI = 11, STI = 12, 
    GETBP = 13, GETSP = 14, INCSP = 15, 
    GOTO = 16, IFZERO = 17, IFNZRO = 18, CALL = 19, TCALL = 20, RET = 21, 
    PRINTI = 22, PRINTC = 23, 
    LDARGS = 24,
    STOP = 25,
    BITAND = 26,BITOR = 27,BITXOR = 28,BITLEFT = 29,BITRIGHT = 30, BITNOT = 31;

  final static int STACKSIZE = 1000;
  
  // Read code from file and execute it

  static void execute(String[] args, boolean trace) 
    throws FileNotFoundException, IOException {
    int[] p = readfile(args[0]);                // Read the program from file
    int[] s = new int[STACKSIZE];               // The evaluation stack
    int[] iargs = new int[args.length-1];
    for (int i=1; i<args.length; i++)           // Push commandline arguments
      iargs[i-1] = Integer.parseInt(args[i]);
    long starttime = System.currentTimeMillis();
    execcode(p, s, iargs, trace);            // Execute program proper
    long runtime = System.currentTimeMillis() - starttime;
    System.err.println("\nRan " + runtime/1000.0 + " seconds");
  }

  // The machine: execute the code starting at p[pc] 

  static int execcode(int[] p, int[] s, int[] iargs, boolean trace) {
    int bp = -999;	// Base pointer, for local variable access 
    int sp = -1;	// Stack top pointer
    int pc = 0;		// Program counter: next instruction
    for (;;) {
      if (trace) 
        printsppc(s, bp, sp, p, pc);                // Print the stack information, current instruction position and instruction number of each step
      switch (p[pc++]) {
      case CSTI:
        s[sp+1] = p[pc++]; sp++; break;             // Put the contents of the current P at the top of the stack
      case ADD: 
        s[sp-1] = s[sp-1] + s[sp]; sp--; break;     // Add the first two numbers at the top of the stack, and the result moves to the next position at the top of the stack together with the top of the stack
      case SUB: 
        s[sp-1] = s[sp-1] - s[sp]; sp--; break;     // subtraction s[sp-1] -s[sp]
      case MUL: 
        s[sp-1] = s[sp-1] * s[sp]; sp--; break;     // multiplication
      case DIV: 
        s[sp-1] = s[sp-1] / s[sp]; sp--; break;     // division
      case MOD: 
        s[sp-1] = s[sp-1] % s[sp]; sp--; break;     // In addition to
      case EQ: 
        s[sp-1] = (s[sp-1] == s[sp] ? 1 : 0); sp--; break;     // Whether the top of the stack is equal to the next one on the top of the stack, equal to 1, otherwise 0, stored in the next one on the top of the stack
      case LT: 
        s[sp-1] = (s[sp-1] < s[sp] ? 1 : 0); sp--; break;     // less than
      case NOT: 
        s[sp] = (s[sp] == 0 ? 1 : 0); break;            // If it is equal to 0, take 1, otherwise take 0
      case DUP: 
        s[sp+1] = s[sp]; sp++; break;                 // Copy a stack top value
      case SWAP: 
        { int tmp = s[sp];  s[sp] = s[sp-1];  s[sp-1] = tmp; } break;    // The first two values are exchanged
      case LDI:                 // load indirect
        s[sp] = s[s[sp]]; break;              // Take the top of the stack as the address in the stack, and assign the value of the address to the top of the stack
      case STI:                 // store indirect, keep value on top
        s[s[sp-1]] = s[sp]; s[sp-1] = s[sp]; sp--; break;     // The top value of the stack is stored in the address represented by the last value of the top of the stack, and the top of the stack becomes the last value, which is the original top value of the stack
      case GETBP:
        s[sp+1] = bp; sp++; break;                      // Add BP as the top of the stack
      case GETSP:
        s[sp+1] = sp; sp++; break;                      // Get the current stack address
      case INCSP:
        sp = sp+p[pc++]; break;                         // Stack address offset, sp+=sp+p[pc]
      case GOTO:
        pc = p[pc]; break;                              // The program goes to p[pc]
      case IFZERO:
        pc = (s[sp--] == 0 ? p[pc] : pc+1); break;      // Consume the top of the stack. If the top of the stack is 0, jump to p[pc], otherwise pc continues to move forward
      case IFNZRO:
        pc = (s[sp--] != 0 ? p[pc] : pc+1); break;      // Contrary to ifzero
      case CALL: { 
        int argc = p[pc++];           // Number of input parameters
        for (int i=0; i<argc; i++)	   // Make room for return address
          s[sp-i+2] = s[sp-i];		   // and old base pointer
        s[sp-argc+1] = pc+1; sp++; 
        s[sp-argc+1] = bp;   sp++; 
        bp = sp+1-argc;                // BP equals the position of the first parameter
        pc = p[pc];                   //
      } break; 
      case TCALL: { 
        int argc = p[pc++];                // Number of new arguments
        int pop  = p[pc++];		   // Number of variables to discard
        for (int i=argc-1; i>=0; i--)	   // Discard variables
          s[sp-i-pop] = s[sp-i];
        sp = sp - pop; pc = p[pc]; 
      } break; 
      case RET: {                           // Get BP and return address, SP returns to the position before calling, and the top of stack is set to res
        int res = s[sp]; 
        sp = sp-p[pc]; bp = s[--sp]; pc = s[--sp]; 
        s[sp] = res; 
      } break; 
      case PRINTI:                        // print int
        System.out.print(s[sp] + " "); break;
      case PRINTC:                       //  print char
        System.out.print((char)(s[sp])); break;
      case LDARGS:                       //  Initialize the stack and store the parameters in the stack
        for (int i=0; i<iargs.length; i++) // Push commandline arguments
          s[++sp] = iargs[i];
        break;
      case STOP:                           // Return to top of stack address
        return sp;
      case BITLEFT: 
          s[sp-1] = s[sp-1] << s[sp]; sp--; break;
      case BITRIGHT: 
          s[sp-1] = s[sp-1] >> s[sp]; sp--; break;
      case BITAND: 
          s[sp-1] = s[sp-1] & s[sp]; sp--; break;
      case BITOR: 
          s[sp-1] = s[sp-1] | s[sp]; sp--; break;
      case BITXOR: 
          s[sp-1] = s[sp-1] ^ s[sp]; sp--; break;
	    case BITNOT: 
          s[sp] = ~s[sp]; break;
      default:                  
        throw new RuntimeException("Illegal instruction " + p[pc-1] 
                                   + " at address " + (pc-1));
      }
    }
  }

  // Print the stack machine instruction at p[pc]

  static String insname(int[] p, int pc) {
    switch (p[pc]) {
    case CSTI:   return "CSTI " + p[pc+1]; 
    case ADD:    return "ADD";
    case SUB:    return "SUB";
    case MUL:    return "MUL";
    case DIV:    return "DIV";
    case MOD:    return "MOD";
    case EQ:     return "EQ";
    case LT:     return "LT";
    case NOT:    return "NOT";
    case DUP:    return "DUP";
    case SWAP:   return "SWAP";
    case LDI:    return "LDI";
    case STI:    return "STI";
    case GETBP:  return "GETBP";
    case GETSP:  return "GETSP";
    case INCSP:  return "INCSP " + p[pc+1];
    case GOTO:   return "GOTO " + p[pc+1];
    case IFZERO: return "IFZERO " + p[pc+1];
    case IFNZRO: return "IFNZRO " + p[pc+1];
    case CALL:   return "CALL " + p[pc+1] + " " + p[pc+2];
    case TCALL:  return "TCALL " + p[pc+1] + " " + p[pc+2] + " " + p[pc+3];
    case RET:    return "RET " + p[pc+1];
    case PRINTI: return "PRINTI";
    case PRINTC: return "PRINTC";
    case LDARGS: return "LDARGS";
    case STOP:   return "STOP";
    default:     return "<unknown>";
    }
  }

  // Print current stack and current instruction

  static void printsppc(int[] s, int bp, int sp, int[] p, int pc) {
    System.out.print("[ ");
    for (int i=0; i<=sp; i++)
      System.out.print(s[i] + " ");
    System.out.print("]");
    System.out.println("{" + pc + ": " + insname(p, pc) + "}"); 
  }

  // Read instructions from a file

  public static int[] readfile(String filename) 
    throws FileNotFoundException, IOException
  {
    ArrayList<Integer> rawprogram = new ArrayList<Integer>();
    Reader inp = new FileReader(filename);
    StreamTokenizer tstream = new StreamTokenizer(inp);
    tstream.parseNumbers();
    tstream.nextToken();
    while (tstream.ttype == StreamTokenizer.TT_NUMBER) {
      rawprogram.add(new Integer((int)tstream.nval));
      tstream.nextToken();
    }
    inp.close();
    final int programsize = rawprogram.size();
    int[] program = new int[programsize];
    for (int i=0; i<programsize; i++)
      program[i] = ((Integer)(rawprogram.get(i))).intValue();
    return program;
  }
}

// Run the machine with tracing: print each instruction as it is executed

class Machinetrace {
  public static void main(String[] args)        
    throws FileNotFoundException, IOException {
    if (args.length == 0) 
      System.out.println("Usage: java Machinetrace <programfile> <arg1> ...\n");
    else
      Machine.execute(args, true);
  }
}
