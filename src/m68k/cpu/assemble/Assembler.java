package m68k.cpu.assemble;

import m68k.cpu.*;
import m68k.cpu.instructions.*;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * User: rnentjes
 * Date: 5-10-14
 * Time: 12:57
 *
 * http://goldencrystal.free.fr/M68kOpcodes.pdf
 * http://www.raidenii.net/files/datasheets/cpu/68000_opcodes.pdf
 * http://oldwww.nvg.ntnu.no/amiga/MC680x0_Sections/index.HTML
 */
public class Assembler {

    private static Map<String, InstructionHandler> commandMapping = new HashMap<String, InstructionHandler>();

    {
        // data transfer instructions
        commandMapping.put("exg", new EXG(null));
        commandMapping.put("lea", new LEA(null));
        commandMapping.put("link", new LINK(null));
        commandMapping.put("move", new MOVE(null));
        commandMapping.put("movea", new MOVE(null));
        commandMapping.put("movem", new MOVEM(null));
        commandMapping.put("moveq", new MOVEQ(null));
        commandMapping.put("movep", new MOVEP(null));
        commandMapping.put("pea", new PEA(null));
        commandMapping.put("unlk", new UNLK(null));

        // arithmetic
        commandMapping.put("abcd", new ABCD(null));
        commandMapping.put("add", new ADD(null));
        commandMapping.put("adda", new ADDA(null));
        commandMapping.put("addi", new ADDI(null));
        commandMapping.put("addq", new ADDQ(null));
        commandMapping.put("addx", new ADDX(null));

        commandMapping.put("sub", new SUB(null));
        commandMapping.put("suba", new SUBA(null));
        commandMapping.put("subi", new SUBI(null));
        commandMapping.put("subq", new SUBQ(null));
        commandMapping.put("subx", new SUBX(null));

        commandMapping.put("cmp", new CMP(null));
        commandMapping.put("cmpa", new CMPA(null));
        commandMapping.put("cmpi", new CMPI(null));
        commandMapping.put("cmpm", new CMPM(null));

        commandMapping.put("divs", new DIVS(null));
        commandMapping.put("divu", new DIVU(null));
        commandMapping.put("muls", new MULS(null));
        commandMapping.put("mulu", new MULU(null));

        commandMapping.put("ext", new EXT(null));

        // logical
        commandMapping.put("and", new AND(null));
        commandMapping.put("andi", new AND(null));
        commandMapping.put("or", new OR(null));
        commandMapping.put("ori", new OR(null));
        commandMapping.put("eor", new EOR(null));
        commandMapping.put("eori", new EOR(null));

        // shifts
        commandMapping.put("asl", new ASL(null));
        commandMapping.put("asr", new ASL(null));
        commandMapping.put("lsl", new ASL(null));
        commandMapping.put("lsr", new ASL(null));
        commandMapping.put("roxl", new ASL(null));
        commandMapping.put("roxr", new ASL(null));
        commandMapping.put("rol", new ASL(null));
        commandMapping.put("ror", new ASL(null));

        // bits
        commandMapping.put("bchg", new BCHG(null));
        commandMapping.put("btst", new BCHG(null));
        commandMapping.put("bclr", new BCHG(null));
        commandMapping.put("bset", new BCHG(null));

        // chk
        commandMapping.put("chk", new CHK(null));

        // negate
        commandMapping.put("negx", new NEGX(null));
        commandMapping.put("clr", new NEGX(null));
        commandMapping.put("neg", new NEGX(null));
        commandMapping.put("not", new NEGX(null));

        // control flow
        // branch
        commandMapping.put("bra", new Bcc(null));
        commandMapping.put("bsr", new Bcc(null));
        commandMapping.put("bhi", new Bcc(null));
        commandMapping.put("bls", new Bcc(null));
        commandMapping.put("bcc", new Bcc(null));
        commandMapping.put("bcs", new Bcc(null));
        commandMapping.put("bne", new Bcc(null));
        commandMapping.put("beq", new Bcc(null));
        commandMapping.put("bvc", new Bcc(null));
        commandMapping.put("bvs", new Bcc(null));
        commandMapping.put("bpl", new Bcc(null));
        commandMapping.put("bmi", new Bcc(null));
        commandMapping.put("bge", new Bcc(null));
        commandMapping.put("blt", new Bcc(null));
        commandMapping.put("bgt", new Bcc(null));
        commandMapping.put("ble", new Bcc(null));
        // dec and brach
        commandMapping.put("dbra", new DBcc(null));
        commandMapping.put("dbsr", new DBcc(null));
        commandMapping.put("dbhi", new DBcc(null));
        commandMapping.put("dbls", new DBcc(null));
        commandMapping.put("dbcc", new DBcc(null));
        commandMapping.put("dbcs", new DBcc(null));
        commandMapping.put("dbne", new DBcc(null));
        commandMapping.put("dbeq", new DBcc(null));
        commandMapping.put("dbvc", new DBcc(null));
        commandMapping.put("dbvs", new DBcc(null));
        commandMapping.put("dbpl", new DBcc(null));
        commandMapping.put("dbmi", new DBcc(null));
        commandMapping.put("dbge", new DBcc(null));
        commandMapping.put("dblt", new DBcc(null));
        commandMapping.put("dbgt", new DBcc(null));
        commandMapping.put("dble", new DBcc(null));

        commandMapping.put("trap",  new TRAP(null));
        commandMapping.put("reset", new RESET(null));
        commandMapping.put("nop",   new NOP(null));
        commandMapping.put("stop",  new STOP(null));

        commandMapping.put("rte",   new RTE(null));
        commandMapping.put("rts",   new RTS(null));
        commandMapping.put("trapv", new TRAPV(null));
        commandMapping.put("rtr",   new RTR(null));
    }

    public SortedSet<Binary> assemble(String [] source) {
        return new TreeSet<Binary>();
    }

    private int pc;
    private AddressSpace mmu;
    private Labels labels;

    public Assembler(AddressSpace mmu) {
        this.mmu = mmu;
        this.labels = new Labels(mmu);
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public DisassembledInstruction parseLine(String line) throws ParseException {
        return parseLine(-1, line);
    }

    private List<String> splitLine(String line) {
        List<String> result = new LinkedList<String>();

        int part = 0; // 1=mnem, 2=op1, 3=op2
        int inPar = 0;
        StringBuilder current = new StringBuilder();
        for(int index = 0; index < line.length(); index++) {
            char ch = line.charAt(index);

            switch(ch) {
                case '\t':
                case ' ':
                    if (part == 1) {
                        result.add(current.toString());
                        current = new StringBuilder();
                        part = 2;
                    }
                    break;
                case '(':
                    current.append(ch);
                    inPar++;
                    break;
                case ')':
                    current.append(ch);
                    inPar--;
                    break;
                case ',':
                    if (inPar == 0) {
                        result.add(current.toString());
                        current = new StringBuilder();
                        part++;
                    } else {
                        current.append(ch);
                    }
                    break;
                default:
                    if (part == 0) {
                        part = 1;
                    }
                    current.append(ch);
                    break;
            }
        }

        result.add(current.toString());

        return result;
    }

    public DisassembledInstruction parseLine(int lineNumber, String line) throws ParseException {
        OperandParser operandParser = new OperandParser();
        String lower = line.trim().toLowerCase();

        if (lower.isEmpty() || lower.startsWith(";")) {
            // comment
            return new DisassembledInstruction(pc, -1, line);
        }

        if (lower.endsWith(":")) {
            labels.addLabel(lower.substring(0, lower.length() - 1), pc);

            return new DisassembledInstruction(pc, -1, line);
        }

        if (lower.contains(" equ ")) {
            String[] parts = lower.split("equ");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Can't parse '" + line + "'");
            } else {
                labels.addLabel(parts[0].trim(), operandParser.parseValue(parts[1].trim()));
            }

            return new DisassembledInstruction(pc, 0, line);
        }

        String mnemonic = lower;
        String command;
        String op1 = "";
        String op2 = "";
        Size size = Size.Unsized;
        int numberOfParts = 0;
        AssembledOperand operand1 = new AssembledOperand("", 0, 0);
        AssembledOperand operand2 = new AssembledOperand("", 0, 0);

        List<String> parts = splitLine(lower);

        if (parts.size() > 0) {
            mnemonic = parts.get(0);
        }

        if (parts.size() == 2) {
            numberOfParts = 1;
            op1 = parts.get(1);
        } else if (parts.size() == 3) {
            numberOfParts = 2;
            op1 = parts.get(1);
            op2 = parts.get(2);
        }

        if (mnemonic.indexOf('.') > -1) {
            command = mnemonic.substring(0, mnemonic.indexOf('.'));
            char sz = mnemonic.charAt(mnemonic.indexOf('.') + 1);

            switch (sz) {
                case 'b':
                    size = Size.Byte;
                    break;
                case 'w':
                    size = Size.Word;
                    break;
                case 'l':
                    size = Size.Long;
                    break;
            }
        } else {
            command = mnemonic;
        }

        if (commandMapping.get(command) == null) {
            throw new ParseException("Mnemonic '" + command + "' not found!", lineNumber);
        } else {
            InstructionHandler handler = commandMapping.get(command);

            if (numberOfParts >= 1) {
                operand1 = operandParser.parse(labels, size, pc, lineNumber, op1);
            }
            if (numberOfParts >= 2) {
                operand2 = operandParser.parse(labels, size, pc, lineNumber, op2);
            }

            AssembledInstruction instruction = new AssembledInstruction(mnemonic, size, operand1, operand2);

            // TODO Assemble
//            DisassembledInstruction disassembled = handler.assemble(pc, instruction, labels);

            // TODO Assemble
//            for (Byte byt : disassembled.bytes()) {
//                mmu.writeByte(pc++, byt);
//            }

            // TODO Assemble
//            return disassembled;
            return null;
        }
    }

    public void printSymbols() {
        labels.printLabels(System.out);
    }

    public static void main(String[] args) throws IOException, ParseException {
        MemorySpace memory = new MemorySpace(512);
        Assembler asm = new Assembler(memory);
        String filename = "test.asm";

        if (args.length > 0) {
            filename = args[0];
        }

        FileOutputStream out = new FileOutputStream("b.out");
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            DisassembledInstruction dis = asm.parseLine(line);

            System.out.println(dis);
        }

        for (int pc = 0; pc < asm.getPc(); pc++) {
            out.write(memory.readByte(pc));
        }

        out.close();
        reader.close();

        asm.printSymbols();

        System.out.println("Dissassembled:");

        MC68000 m68 = new MC68000();

        m68.setAddressSpace(memory);
        m68.reset();

        int pc = 0;
        boolean done = false;
        while (pc < asm.getPc()) {
            int opcode = m68.readMemoryWord(pc);
            Instruction inst = m68.getInstructionFor(opcode);

            if (inst != null) {
                DisassembledInstruction di = inst.disassemble(pc, opcode);

                pc += di.size();

                System.out.println(di);
            } else {
                done = true;
            }
        }
    }

}
