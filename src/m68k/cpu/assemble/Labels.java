package m68k.cpu.assemble;

import m68k.cpu.Size;
import m68k.memory.AddressSpace;

import java.io.PrintStream;
import java.util.*;

/**
 * User: rnentjes
 * Date: 8-3-15
 * Time: 12:25
 */
public class Labels {

    private static class ExpectedLabel {
        public String name;
        public int address;
        public boolean relative;
        public Size size;

        public ExpectedLabel(int address, String name, boolean relative, Size size) {
            this.address = address;
            this.name = name;
            this.relative = relative;
            this.size = size;
        }
    }

    private AddressSpace mmu;
    private Map<String, Integer> labels = new HashMap<String, Integer>();
    private Map<String, Set<ExpectedLabel>> expected = new HashMap<String, Set<ExpectedLabel>>();

    public Labels(AddressSpace mmu) {
        this.mmu = mmu;
    }

    public void addLabel(String name, int address) {
        // check exists
        // update in expected
        labels.put(name, address);

        // from Assembler
        Set<ExpectedLabel> usages = expected.get(name);

        if (usages != null) {
            for (ExpectedLabel usage : usages) {
                if (usage.relative) {
                    switch (usage.size) {
                        case Byte:
                            mmu.writeByte(usage.address, address - usage.address - 1);
                            break;
                        case Unsized:
                        case Word:
                            mmu.writeWord(usage.address, address - usage.address);
                            break;
                        case Long:
                            mmu.writeLong(usage.address, address - usage.address);
                            break;
                    }
                } else {
                    mmu.writeLong(usage.address, address);
                }
            }

            expected.remove(name);
        }
    }

    public boolean hasLabel(String name) {
        return labels.containsKey(name);
    }

    public int getLabel(String name, int pc, boolean relative, Size size) {
        Integer result = labels.get(name);

        if (result == null) {
            result = -1;

            Set<ExpectedLabel> expectedLabels = expected.get(name);

            if (expectedLabels == null) {
                expectedLabels = new HashSet<ExpectedLabel>();

                expected.put(name, expectedLabels);
            }

            expectedLabels.add(new ExpectedLabel(pc, name, relative, size));
        }

        return result;
    }

    public void printLabels(PrintStream out) {
        Set<String> sortedLabels = new TreeSet<String>();

        sortedLabels.addAll(labels.keySet());
        for (String label : sortedLabels) {
            int address = labels.get(label);
            out.println(String.format("%32s  %08x", label, address));
        }
        out.println("Expected:");
        for (Map.Entry<String, Set<ExpectedLabel>> entry : expected.entrySet()) {
            for (ExpectedLabel label : entry.getValue()) {
                out.println(String.format("%32s  %08x", entry.getKey(), label.address));
            }
        }
    }

}
