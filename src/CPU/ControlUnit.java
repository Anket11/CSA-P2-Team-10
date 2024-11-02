package CPU;

public class ControlUnit {

    // Fields
    private int opcode, I, R, IX, address;
    private int Rx, Ry;
    private int AL, LR, Count;

    // Constructor
    public ControlUnit(String BinaryString) {
        decodeBinaryString(BinaryString);
    }

    public void decodeInstruction(int IR) {
        // Convert the instruction to a binary string for easy splitting
        String BinaryString = ToBinaryString(IR);
        // Decode the instruction and extract fields
        decodeBinaryString(BinaryString);
        // Extra decoding for MULTI/DIV instructions
        decodeMultiDiv(BinaryString);
        // Print the result for easy debugging
        logDecodedValues();
    }

    // Getters
    public int getOpcode() {
        return opcode;
    }


    public int getRx() {
        return Rx;
    }

    public int getRy() {
        return Ry;
    }

    public int getAL() {
        return AL;
    }

    public int getAddress() {
        return address;
    }

    public int getCount() {
        return Count;
    }

    public int getR() {
        return R;
    }

    public int getI() {
        return I;
    }

    public int getIX() {
        return IX;
    }

    public int getLR() {
        return LR;
    }



    private void decodeMultiDiv(String BinaryString) {
        Rx = Integer.valueOf(BinaryString.substring(6, 8), 2);
        Ry = Integer.valueOf(BinaryString.substring(8, 10), 2);
        AL = Integer.valueOf(BinaryString.substring(8, 9), 2);
        LR = Integer.valueOf(BinaryString.substring(9, 10), 2);
        Count = Integer.valueOf(BinaryString.substring(12, 16), 2);
    }

    private void decodeBinaryString(String BinaryString) {
        opcode = Integer.valueOf(BinaryString.substring(0, 6), 2);
        R = Integer.valueOf(BinaryString.substring(6, 8), 2);
        IX = Integer.valueOf(BinaryString.substring(8, 10), 2);
        I = Integer.valueOf(BinaryString.substring(10, 11), 2);
        address = Integer.valueOf(BinaryString.substring(11, 16), 2);
    }



    private void logDecodedValues() {
        System.out.println(opcode + "\t" + R + "\t" + IX + "\t" + I + "\t" + address);
    }

    private String ToBinaryString(int value) {
        String binaryString = Integer.toBinaryString(value); // Convert to binary string
        String Stringlength = "16";
        String format = "%0numberd".replace("number", Stringlength);
        return String.format(format, Long.valueOf(binaryString));
    }
}
