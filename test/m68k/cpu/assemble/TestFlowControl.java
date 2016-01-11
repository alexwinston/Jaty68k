package m68k.cpu.assemble;

/**
 * User: rnentjes
 * Date: 8-10-14
 * Time: 11:39
 */
public class TestFlowControl extends AssemblerTestCase {

    public void testBra() {
        asm.setPc(0);
        checkAsm("LOOP:", "0000");
        checkAsm("BRA LOOP", "60fe");
    }


}
