package m68k.cpu.assemble;

import m68k.cpu.Size;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Date: 7-10-14
 * Time: 21:29
 */
public class OperandParser {

    private static Pattern labelPattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]+");

    private static Map<String, AddressingMode> modeMapping = new HashMap<String, AddressingMode>();

    {
        modeMapping.put("v", AddressingMode.IMMEDIATE);
        modeMapping.put("vv", AddressingMode.IMMEDIATE);
        modeMapping.put("a", AddressingMode.IMMEDIATE_ADDRESS);
        modeMapping.put("d", AddressingMode.IMMEDIATE_DATA);
        modeMapping.put("i", AddressingMode.INDIRECT);
        modeMapping.put("-i", AddressingMode.INDIRECT_PRE);
        modeMapping.put("i+", AddressingMode.INDIRECT_POST);

        modeMapping.put("vw", AddressingMode.ABSOLUTE_NEAR);
        modeMapping.put("vl", AddressingMode.ABSOLUTE_FAR);

        modeMapping.put("va", AddressingMode.INDIRECT_DISP);
        modeMapping.put("vi", AddressingMode.INDIRECT_DISP);
        modeMapping.put("vad", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vid", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vadw", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vadl", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vaaw", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vaal", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vidw", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vidl", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("viaw", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vial", AddressingMode.INDIRECT_INDEX);
        modeMapping.put("vp", AddressingMode.PC_DISP);
        modeMapping.put("vpd", AddressingMode.PC_INDEX);
        modeMapping.put("vpa", AddressingMode.PC_INDEX);
        modeMapping.put("vpdw", AddressingMode.PC_INDEX);
        modeMapping.put("vpdl", AddressingMode.PC_INDEX);
        modeMapping.put("vpaw", AddressingMode.PC_INDEX);
        modeMapping.put("vpal", AddressingMode.PC_INDEX);

        modeMapping.put("s", AddressingMode.SR);
        modeMapping.put("c", AddressingMode.CCR);
    }

    /**
     * Operand types:
     * v - value
     * a - address register
     * i - indirect address register
     * d - data register
     * p - pc register
     * s - sr register
     * c - ccr register
     * w - word
     * l - long
     * + - post increment
     * - - pre increment
     * b - could be label
     */
    private static class Part {
        public char type = ' ';
        public StringBuilder text = new StringBuilder();
    }

    private List<Part> parts;
    private Part current;

    private void clear() {
        parts = new LinkedList<Part>();
        current = new Part();
    }

    private void newPart() {
        if (current.type != ' ') {
            parts.add(current);
        }

        current = new Part();
    }

    private void setType(char t) {
        if (current.type == ' ') {
            current.type = t;
        }
    }

    public AssembledOperand parse(Labels labels, Size size, int pc, int lineNumber, String operand) throws ParseException {
        clear();
        String lower = operand.trim().toLowerCase();
        char last = ' ';

        for (int index = 0; index < lower.length(); index++) {
            char ch = lower.charAt(index);
            char next = ' ';

            if (index < lower.length() - 1) {
                next = lower.charAt(index+1);
            }

            switch(ch) {
                case ',':
                case '.':
                case '(':
                case ')':
                case '/':
                    newPart();
                    break;
                case '+':
                    if (last == ')') {
                        setType(ch);
                    }
                    newPart();
                    current.text.append(ch);
                    break;
                case '-':
                    if (next == '(') {
                        setType(ch);
                    }
                    newPart();
                    current.text.append(ch);
                    break;
                case 'a':
                    if (last == '(') {
                        setType('i');
                    } else {
                        setType(ch);
                    }
                    current.text.append(ch);
                    break;
                case 'd':
                case 'p':
                case 'c':
                case 's':
                case 'w':
                case 'l':
                    setType(ch);
                    current.text.append(ch);
                    break;
                case '#':
                    setType('v');
                    break;
                case '%':
                case '$':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    setType('v');
                    current.text.append(ch);
                    break;
                case 'r':
                    current.text.append(ch);
                    break;
            }

            if (ch != ' ') {
                last = ch;
            }
        }
        newPart();

        AddressingMode mode = modeMapping.get(descriptor());

        int bytes = 0;
        int memory_read = 0;
        Conditional conditional = Conditional.NA;
        int register = 0;
        int ext_reg = 0;
        int ext_data = 0;
        Size ext_size = Size.Unsized;

        if (mode == null) {
            if (parts.isEmpty()) {
                if (couldBeLabel(operand)) {
                    mode = AddressingMode.LABEL;
                } else {
                    throw new ParseException("Unable to parse expression '" + operand + "'", lineNumber);
                }
            } else {
                boolean registerList = true;

                for (Part part : parts) {
                    if (part.type != 'd' && part.type != 'a') {
                        registerList = false;
                    }
                }

                if (registerList) {
                    mode = AddressingMode.REGISTER_LIST;
                } else {
                    if (couldBeLabel(operand)) {
                        mode = AddressingMode.LABEL;

                        if (labels.hasLabel(operand)) {
                            memory_read = labels.getLabel(operand, 0, false, size);

                            if (memory_read == -1) {
                                throw new ParseException("Unkown label "+operand, lineNumber);
                            }
                        } else {
                            memory_read = 0;
                        }
                    } else {
                        throw new ParseException("Unable to parse expression '" + operand + "'", lineNumber);
                    }
                }
            }
        }

        switch(mode) {
            case IMMEDIATE:
                memory_read = parseValue(parts.get(parts.size()-1).text.toString());
                register = 4;
                if (size == Size.Long) {
                    bytes = 4;
                } else {
                    bytes = 2;
                }
                break;
            case IMMEDIATE_DATA:
            case IMMEDIATE_ADDRESS:
            case INDIRECT:
            case INDIRECT_POST:
                register = Integer.parseInt(parts.get(0).text.toString().substring(1));
                break;
            case INDIRECT_PRE:
                register = Integer.parseInt(parts.get(1).text.toString().substring(1));
                break;
            case ABSOLUTE_NEAR:
                memory_read = parseValue(parts.get(0).text.toString());
                bytes = 2;
                break;
            case ABSOLUTE_FAR:
                memory_read = parseValue(parts.get(0).text.toString());
                bytes = 4;
                register = 1;
                break;
            case INDIRECT_DISP:
                memory_read = parseValue(parts.get(0).text.toString());
                register = Integer.parseInt(parts.get(1).text.toString().substring(1));
                bytes = 2;
                break;
            case INDIRECT_INDEX:
                ext_data = parseValue(parts.get(0).text.toString());
                ext_reg = Integer.parseInt(parts.get(2).text.toString().substring(1));
                register = Integer.parseInt(parts.get(1).text.toString().substring(1));
                if (parts.size() > 3) {
                    ext_size = parts.get(3).type == 'w' ? Size.Word : Size.Long;
                } else {
                    ext_size = Size.Word;
                }
                break;
            case PC_DISP:
                memory_read = (parseValue(parts.get(0).text.toString()) - pc - 2) & 0xffff;
                register = 2;
                bytes = 2;
                break;
            case PC_INDEX:
                ext_data = parseValue(parts.get(0).text.toString());
                ext_reg = Integer.parseInt(parts.get(2).text.toString().substring(1));
                register = 3;
                if (parts.size() > 3) {
                    ext_size = parts.get(3).type == 'w' ? Size.Word : Size.Long;
                } else {
                    ext_size = Size.Word;
                }
                break;
            case REGISTER_LIST:
                int lastReg = -1;
                char lastType = ' ';
                for (Part part : parts) {
                    int reg = -1;
                    boolean dataReg = part.type == 'd';

                    if (part.text.charAt(0) == '-') {
                        // lastReg to reg
                        reg = Integer.parseInt(Character.toString(part.text.charAt(2)));

                        if (lastType == 'd' && !dataReg) {
                            for (; lastReg <= 7; lastReg++) {
                                int or = 1 << lastReg;
                                register |= or;
                            }
                            lastReg = 0;
                            for (; lastReg <= reg; lastReg++) {
                                int or = 1 << lastReg << 8;
                                register |= or;
                            }
                        } else {
                            for (; lastReg <= reg; lastReg++) {
                                int or = 1 << lastReg;
                                if (!dataReg) {
                                    or = or << 8;
                                }
                                register |= or;
                            }
                        }
                    } else {
                        reg = Integer.parseInt(Character.toString(part.text.charAt(1)));

                        int or = 1 << reg;
                        if (!dataReg) {
                            or = or << 8;
                        }

                        register |= or;
                    }

                    lastReg = reg;
                    lastType = part.type;
                }
                break;
            case LABEL:
                mode = AddressingMode.IMMEDIATE;
                if (size == Size.Long) {
                    bytes = 4;
                } else {
                    bytes = 2;
                }
                register = 4;
                break;
        }

        if (mode == AddressingMode.PC_INDEX) {
            // relative to pc
            memory_read = ((ext_data - pc -2) & 0xff) | (ext_reg << 12);
            if (ext_size == Size.Long) {
                memory_read |= 0x800;
            }
            bytes = 2;
        } else if (mode == AddressingMode.INDIRECT_INDEX) {
            memory_read = (ext_data & 0xff) | (ext_reg << 12);
            if (ext_size == Size.Long) {
                memory_read |= 0x800;
            }
            bytes = 2;
        }

        return new AssembledOperand(lower, bytes, memory_read, mode, conditional, register);
    }

    private boolean couldBeLabel(String value) {
        for (int index = 0; index < value.length(); index++) {
            char ch = value.charAt(index);

            if (!(ch >= 'a' && ch <='z') && index == 0) {
                return false;
            } if (!(ch >= 'a' && ch <='z') && !(ch >= '0' && ch <='9') && !(ch == '_')) {
                return false;
            }
        }

        return true;
    }

    public int parseValue(String value) {
        if (value.charAt(0) == '$') {
            return Integer.parseInt(value.substring(1), 16);
        } else if (value.charAt(0) == '%') {
            return Integer.parseInt(value.substring(1), 2);
        } else {
            return Integer.parseInt(value);
        }
    }

    public String descriptor() {
        StringBuilder result = new StringBuilder();

        for (Part part : parts) {
            result.append(part.type);
        }

        return result.toString();
    }
    public List<Part> parts() {
        return parts;
    }

    public static void main(String[] args) throws ParseException {
        OperandParser parser = new OperandParser();

        String [] strings = {
                "#1234",
                "a0",
                "(a0)",
                "( a0)",
                "-(a0)",
                "(a0)+",
                "11(a0)",
                "(11)(a0)",
                "(12, a1)",
                "(123).l",
                "(123).w",
                "123.l",
                "123.w",
                "10(PC)",
                "(10)(PC)",
                "10(PC, d0.w)",
                "10(PC, d0.l)",
                "10(PC, A4.w)",
                "10(PC, A5.l)",
                "(10)(PC, d0.w)",
                "(10)(PC, d0.l)",
                "($10)(PC, A4.w)",
                "(%10)(PC, A5.l)",
                "sr",
                "ccr",
                "(10, a2, d1)",
                "10(a2, d1)",
                "d1-d2/d5",
                "a1/a3/a5/a7",
                "xxxx"
        };

        int lineNumber = 1;
        for (String str : strings) {
            parser.parse(null, Size.Word, 0, lineNumber++, str);

            System.out.println(str + " -> " + parser.descriptor());

            List<Part> parts = parser.parts();
            for (Part part : parts) {
                System.out.println("\t"+part.type+" - "+part.text);
            }
        }

    }
}
