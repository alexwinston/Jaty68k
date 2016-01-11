package m68k.cpu.assemble;

import m68k.cpu.Size;

/**
 * User: rnentjes
 * Date: 5-10-14
 * Time: 14:25
 */
public class LabelUsage {

    public final String name;
    public final int address;
    public final Size size;
    public final boolean relative;

    public LabelUsage(String name, int address, Size size, boolean relative) {
        this.name = name;
        this.address = address;
        this.size = size;
        this.relative = relative;
    }

}
