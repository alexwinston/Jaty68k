package m68k.cpu.assemble;

import junit.framework.TestCase;
import m68k.cpu.DisassembledInstruction;
import m68k.memory.MemorySpace;

import java.text.ParseException;

/**
 * User: rnentjes
 * Date: 8-10-14
 * Time: 11:56
 */
public abstract class AssemblerTestCase extends TestCase {

    MemorySpace memory;
    Assembler asm;

    public void setUp() {
        memory = new MemorySpace(1);
        asm = new Assembler(memory);
    }

    protected void checkAsm(String asmline, String bytes) {

        try {
            DisassembledInstruction di = asm.parseLine(asmline);
            int index = 0;

            StringBuilder checkBytes = new StringBuilder();
            // TODO Assemble
//            for (Byte byt : di.bytes()) {
            for (Byte byt : new byte[0]) {
                if (index > 0 && index % 2 == 0) {
                    checkBytes.append(" ");
                }
                String hex = Integer.toHexString((byt & 0xff));

                if (hex.length() == 1) {
                    checkBytes.append("0");
                }
                checkBytes.append(hex);
                index++;
            }

            assertEquals("Wrong opcodes: '" + asmline + "'", bytes.trim().toLowerCase(), checkBytes.toString().trim().toLowerCase());
        } catch (ParseException e) {
            assertFalse("ParseException in line "+ e.getErrorOffset() + ": '" + asmline + "' -> " + bytes , true);
        }

    }

}
