package m68k.cpu.assemble;

import m68k.cpu.InstructionHandler;
import m68k.cpu.instructions.EXG;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 9-10-14
 * Time: 20:29
 */
public class Mnemonics {

    public static class Mnemonic {
        public final InstructionHandler handler;
        public final int src;
        public final int dst;

        public Mnemonic(InstructionHandler handler, int src, int dst) {
            this.handler = handler;
            this.src = src;
            this.dst = dst;
        }
    }

    public static int DATA_REGISTER = 1;
    public static int ADDRESS_REGISTER = 2;

    private static Map<String, Mnemonic> mapping = new HashMap<String, Mnemonic>();
    static {
        mapping.put("exg", new Mnemonic(new EXG(null), DATA_REGISTER | ADDRESS_REGISTER, DATA_REGISTER | ADDRESS_REGISTER));
    }

}
