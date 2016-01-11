package m68k.cpu.assemble;

/**
 * User: rnentjes
 * Date: 5-10-14
 * Time: 13:04
 */
public enum AddressingMode {
    NA(0, 0),
    IMMEDIATE(1, 7),
    IMMEDIATE_DATA(1, 0),
    IMMEDIATE_ADDRESS(1, 1),
    INDIRECT(1, 2),
    INDIRECT_POST(2, 3),
    INDIRECT_PRE(2, 4),
    INDIRECT_DISP(2, 5),
    INDIRECT_INDEX(3, 6),
    ABSOLUTE_NEAR(2, 7),
    ABSOLUTE_FAR(2 ,7),
    PC_DISP(2, 7),
    PC_INDEX(3, 7),
    SR(1, 0),
    CCR(1, 0),
    REGISTER_LIST(1, 0),
    LABEL(1,0)
    ;

    private int requiredOperants;
    private int bits;

    AddressingMode(int requiredOperants, int bits) {
        this.requiredOperants = requiredOperants;
        this.bits = bits;
    }

    public int getRequiredOperants() {
        return requiredOperants;
    }

    public int bits() {
        return bits;
    }
}
