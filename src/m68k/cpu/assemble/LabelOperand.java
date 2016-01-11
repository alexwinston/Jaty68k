package m68k.cpu.assemble;

/**
 * Date: 9-10-14
 * Time: 20:40
 */
public class LabelOperand extends AssembledOperand {
    public LabelOperand(int pc, String operand) {
        super(operand, 0, pc);
    }
}
