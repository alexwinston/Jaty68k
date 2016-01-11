package m68k.cpu.assemble;

import java.nio.ByteBuffer;
import java.util.Comparator;

/**
 * User: rnentjes
 * Date: 5-10-14
 * Time: 12:59
 */
public class Binary implements Comparator<Binary> {

    private long startAddress;
    private ByteBuffer data;

    public Binary(long startAddress, ByteBuffer data) {
        this.startAddress = startAddress;
        this.data = data;
    }

    public long getStartAddress() {
        return startAddress;
    }

    public ByteBuffer getData() {
        return data;
    }

    @Override
    public int compare(Binary o1, Binary o2) {
        return o1.startAddress < o2.startAddress ? -1 :
                o1.startAddress > o2.startAddress ? 1 : 0;
    }

}
