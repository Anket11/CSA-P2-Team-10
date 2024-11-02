package CPU;

import Memory.Memory;
import java.util.logging.Logger;

public class ALU {

    private final Logger logging = Logger.getLogger("ALU");

    // ALU Properties
    public int OPCode;
    public int A;
    public int B;
    public int output;
    public int HIResult;
    public int LOResult;
    public boolean CC0, CC1, CC2, CC3;

    // Constructor: Initialize ALU properties
    public ALU() {
        OPCode = 0;
        A = 0;
        B = 0;
        output = 0;
        resetConditionCodes();
    }

    public void Calc(int OPCode, int A, int B) {
        this.OPCode = OPCode;
        this.A = A;
        this.B = B;
        resetConditionCodes();

        switch (this.OPCode) {
            case 4, 6 -> performAddition();
            case 5, 7 -> performSubtraction();
            case 20 -> handleMultiplication();
            case 21 -> handleDivision();
            case 23 -> output = A & B;
            case 24 -> output = A | B;
            case 25 -> output = ~A;
            default -> logging.warning("Unsupported: " + this.OPCode);
        }
    }

    private void resetConditionCodes() {
        CC0 = false;
        CC1 = false;
        CC2 = false;
        CC3 = false;
    }

    private void performAddition() {
        output = A + B;
        CC0 = checkOverflow(output);
    }

    private void performSubtraction() {
        output = A - B;
        CC1 = checkUnderflow(output);
    }

    private boolean checkOverflow(int value) {
        return Integer.toBinaryString(value).length() > 16;
    }

    private boolean checkUnderflow(int value) {
        return value < 0;
    }

    private void handleMultiplication() {
        int MULResult = this.A * this.B;
        String MULRes = ToBinaryString(MULResult, 32);
        String HI = MULRes.substring(0, 16);
        String LO = MULRes.substring(16, 32);
        HIResult = Integer.parseInt(HI, 2);
        LOResult = Integer.parseInt(LO, 2);
        CC0 = false;
    }

    private void handleDivision() {
        if (this.B == 0) {
            CC2 = true;
            return;
        }
        try {
            HIResult = this.A / this.B;
            LOResult = this.A % this.B;
        } catch (Exception e) {
            System.out.println("DIV error.");
            CC2 = true;
        }
    }

    public String ToBinaryString(int value, int length) {
        String binaryString = Integer.toBinaryString(value);
        int paddingLength = length - binaryString.length();
        return "0".repeat(Math.max(0, paddingLength)) + binaryString;
    }
}
