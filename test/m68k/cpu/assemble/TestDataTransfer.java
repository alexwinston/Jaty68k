package m68k.cpu.assemble;

/**
 * User: rnentjes
 * Date: 8-10-14
 * Time: 11:39
 */
public class TestDataTransfer extends AssemblerTestCase {

    public void testExg() {
        checkAsm("adda.w (a0), a1", "d2d0");
//        checkAsm("adda.w ($fffff100).w, a1", "d2F8 f100");
//        checkAsm("adda.l ($FFFFF100).l, a1", "D3F9 F100");
        checkAsm("addq.b #1, ($FFFFF100).w", "5238 F100");
        checkAsm("adda.w #$1337, a1", "D2FC 1337");
        checkAsm("addi.b #$20, d1", "0601 0020");
        checkAsm("exg d0,d1", "c141");
        checkAsm("exg d0,d2", "c142");
        checkAsm("exg a0,d2", "c588");
        checkAsm("exg a0,a1", "c149");
        checkAsm("exg d4,a0", "c988");
    }

    public void testLea() {
        checkAsm("lea (a0), a0", "41d0");
        checkAsm("lea (10, a0), a4", "49e8 000a");
        checkAsm("lea (10, a0, d3.w), a5", "4bf0 300a");
        checkAsm("lea (10, a3, d7.w), a3", "47f3 700a");
    }

    public void testLink() {
        checkAsm("link a5,#-8", "4e55 fff8");
    }

    public void testMove() {
        asm.setPc(0);
        checkAsm("move.l a2, d0", "200a");
        checkAsm("move.l (a2), d0", "2012");
        checkAsm("move.w -(a2), d1", "3222");
        checkAsm("move.w (a2)+, d2", "341a");
        checkAsm("move.w 10(a2), d3", "362a 000a");
        checkAsm("move.w (10, a2), d3", "362a 000a");
        checkAsm("move.w (10, a2, d1), d4", "3832 100a");
        checkAsm("move.w 123.w, d5", "3a38 007b");
        checkAsm("move.w 12345.l, d5", "3a39 0000 3039");
        checkAsm("move.w 10(PC), d6", "3c3a ffea"); // pc disp test!

        checkAsm("move.w (10, PC, d4), d7", "3e3b 40e6");
        checkAsm("move.w 10(a2, d1), d4", "3832 100a");

        checkAsm("move.w d0, a0", "3040");
        checkAsm("move.l d0, d2", "2400");
        checkAsm("move.l #$1234, d0", "203c 0000 1234");
    }

    public void testMovea() {
        checkAsm("movea.l d2, a2", "2442");
        checkAsm("movea.w d2, a2", "3442");
        checkAsm("movea.l d7, a6", "2c47");
        checkAsm("movea.w -(a2), a1", "3262");
        checkAsm("movea.l (a2)+, a2", "245a");
        checkAsm("movea.w 10(a2), a3", "366a 000a");
        checkAsm("movea.w (10, a2), a3", "366a 000a");
        checkAsm("movea.w (10, a2, d1), a4", "3872 100a");
    }

    public void testMovem() {
        checkAsm("movem.w d2/d3, (a0)", "4890 000c");
        checkAsm("movem.w (a0), d2/d3", "4c90 000c");
        checkAsm("movem.l (a5), d1/d3-d6/d7-a2/a5",         "4cd5 27fa");
        checkAsm("movem.l d1/d3-d6/d7-a3/a6, ($400, a2)",   "48ea 4ffa 0400");
        checkAsm("movem.l d1-a7, ($10, a2, d0)",            "48f2 fffe 0010");
    }

    public void testMoveq() {
        checkAsm("moveq #1, d0", "7001");
        checkAsm("moveq #40, d7", "7e28 ");
    }

    public void testMovep() {
        checkAsm("movep.l d3, (4,a2)", "07ca 0004");
        checkAsm("movep.w d4, (6,a3)", "098b 0006");
    }

    public void testPea() {
        checkAsm("pea (a0)",            "4850");
        checkAsm("pea (10, a0)",        "4868 000a");
        checkAsm("pea (10, a0, d3.w)",  "4870 300a");
        checkAsm("pea (10, a3, d7.w)",  "4873 700a");
    }

    public void testUnlk() {
        checkAsm("unlk a1", "4e59");
        checkAsm("unlk a6", "4e5e");
    }


}
