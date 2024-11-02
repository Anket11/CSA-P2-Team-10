package CPU;


public class Register {

    private int value, length;
    private String name;

    public Register(int value, int length, String name) {
        this.length = length;
        this.name = name;
        setValue(value);
    }

    public int getValue() {
        return value;
    }

    public String ToBinaryString() {
        String binaryValue = Integer.toBinaryString(value);
        String format = "%0" + length + "d";
        return String.format(format, Long.valueOf(binaryValue));
    }

    public String ToBinaryString(int value, int length) {
        String binaryValue = Integer.toBinaryString(value);
        if (binaryValue.length() == 32 && binaryValue.startsWith("1")) {
            return binaryValue;
        }
        String format = "%0" + length + "d";
        return String.format(format, Long.valueOf(binaryValue));
    }

    public void setValue(int Value) {
        if (Math.pow(2, length) > Value && Value >= 0) {
            this.value = Value;
            logRegisterState();
        } else {
            logInvalidState(Value);
        }
    }

    public void setValue(String Value) {
        int intValue = Integer.valueOf(Value, 2);
        setValue(intValue);
    }

    public void setValue(int Value, boolean OverFlowSet) {
        if (Math.pow(2, length) > Value && Value >= 0) {
            this.value = Value;
            logRegisterState();
        } else {
            if (OverFlowSet) {
                handleOverflowSet(Value);
            } else {
                logInvalidState(Value);
            }
        }
    }

    // Private helper method to log register state.
    private void logRegisterState() {
        System.out.println(name + "=>" + ToBinaryString() + "(" + value + ")");
    }

    // Private helper method to log invalid state.
    private void logInvalidState(int Value) {
        System.out.println("INVALID " + name + "=>" + ToBinaryString() + "(" + Value + ")");
    }

    // Private helper method to handle overflow settings.
    private void handleOverflowSet(int Value) {
        String binaryValue = ToBinaryString(Value, 32);
        String truncatedValue = binaryValue.substring(binaryValue.length() - length);
        setValue(truncatedValue);
    }
}
