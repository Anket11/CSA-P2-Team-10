package CPU;

import Interface.ConsoleRegisterCollection;
import Interface.IOBuffer;
import Memory.Memory;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Bus {

    // Constants and Class Variables
    private final Logger logging = Logger.getLogger("Bus");
    private Components components;
    private Memory dataMemory;
    private boolean isHalt = false;
    private IOBuffer KeyboardBuffer = new IOBuffer("Keyboard");
    private IOBuffer CardReaderBuffer = new IOBuffer("Card Reader");
    public ConsoleRegisterCollection ConsoleRegisterCollection = new ConsoleRegisterCollection();
    public String OutputString = "";
    public ArrayList<Integer> BreakPointList = new ArrayList<>(100);

    // Constructor
    public Bus(Components components, Memory dataMemory) {
        this.components = components;
        this.dataMemory = dataMemory;
    }

    // Status and Control Methods
    public boolean getHaltStatus() {
        return isHalt;
    }

    public void setHalt() {
        isHalt = true;
    }

    // BreakPoint Management
    public void setBreakPoint(int BreakPointPC) {
        BreakPointList.add(BreakPointPC);
    }

    public boolean BreakPointCheck(int PC) {
        return BreakPointList.contains(PC);
    }

    public void removeBreakPoint(int PC) {
        BreakPointList.remove(Integer.valueOf(PC));
    }

    public Object[] getBreakPointList() {
        return BreakPointList.toArray();
    }

    // Instruction Execution
    public void tik() {
        components.getMAR().setValue(components.getPC().getValue());
        components.getPC().incrementOne();
        components.getMBR().setValue(dataMemory.get(components.getMAR().getValue()));
        components.getIR().setValue(components.getMBR().getValue());
        components.getCU().decodeInstruction(components.getIR().getValue());
        executeInstruction(calculateEA());
    }

    public void run() {
        while (true) {
            tik();
            if (BreakPointCheck(components.PC.getValue()) || isHalt) {
                break;
            }
        }
    }

    public void evaulateInstruction(int instruction) {
        components.getIR().setValue(instruction);
        components.getCU().decodeInstruction(components.getIR().getValue());
        executeInstruction(calculateEA());
    }

    // Effective Address Calculations
    private int calculateEA() {
        int address = components.getCU().getAddress();
        if (components.getCU().getI() == 0) {
            return (components.getCU().getIX() == 0) ? address : address + components.getIXRegister().getValue();
        } else if (components.getCU().getI() == 1) {
            return (components.getCU().getIX() == 0) ? dataMemory.get(address) : dataMemory.get(address + components.getIXRegister().getValue());
        } else {
            logging.severe("I has trouble");
            System.out.println("I has trouble");
            return -1;
        }
    }

    private int calculateEA_LDX() {
        int address = components.getCU().getAddress();
        if (components.getCU().getI() == 0 || components.getCU().getIX() == 0) {
            return address;
        } else {
            return dataMemory.get(address);
        }
    }

    // Instruction Execution - Main Execution Logic
    private void executeInstruction(int ea) {
        switch (components.getCU().getOpcode()) {
            case 0: executeHalt(); break;
            case 1: executeLoad(ea); break;
            case 2: executeStore(ea); break;
            case 3: executeAddressLoad(ea); break;
            case 4: executeAMR(ea); break;
            case 5: executeSMR(ea); break;
            case 6: executeAIR(); break;
            case 7: executeSIR(); break;
            case 10: executeJZ(ea); break;
            case 11: executeJNE(ea); break;
            case 12: executeJCC(ea); break;
            case 13: executeJMA(ea); break;
            case 14: executeJSR(ea); break;
            case 15: executeRFS(); break;
            case 16: executeSOB(ea); break;
            case 17: executeJGE(ea); break;
            case 20: executeMLT(); break;
            case 21: executeDVD(); break;
            case 22: executeTRR(); break;
            case 23: executeAND(); break;
            case 24: executeORR(); break;
            case 25: executeNOT(); break;
            case 31: executeSRC(); break;
            case 32: executeRRC(); break;
            case 41: executeLoadIX(ea); break;
            case 42: executeStoreIX(ea); break;
            case 61: executeIN(); break;
            case 62: executeOUT(); break;
            default: break;
        }
    }

    // Instruction Execution - Specific Instructions
    private void executeHalt() {
        isHalt = true;
        components.PC.setValue(6);
        System.out.println("BUS:HALT");
    }

    private void executeLoad(int ea) {
        components.getGPRRegister().setValue(dataMemory.get(ea));
    }

    private void executeStore(int ea) {
        dataMemory.set(ea, components.getGPRRegister().getValue(), true);
    }

    private void executeAddressLoad(int ea) {
        components.getGPRRegister().setValue(ea);
    }

    private void executeAMR(int ea) {
        components.ALU.Calc(components.getCU().getOpcode(), components.getGPRRegister().getValue(), dataMemory.get(ea));
        components.CC0.set(components.ALU.CC0);
        components.getGPRRegister().setValue(components.ALU.output, components.ALU.CC0);
    }

    private void executeSMR(int ea) {
        components.ALU.Calc(components.getCU().getOpcode(), components.getGPRRegister().getValue(), dataMemory.get(ea));
        components.CC1.set(components.ALU.CC1);
        components.getGPRRegister().setValue(components.ALU.output, components.ALU.CC1);
    }

    private void executeAIR() {
        components.ALU.Calc(components.getCU().getOpcode(), components.getGPRRegister().getValue(), components.getCU().getAddress());
        components.CC0.set(components.ALU.CC0);
        components.getGPRRegister().setValue(components.ALU.output, components.ALU.CC0);
    }

    private void executeSIR() {
        components.ALU.Calc(components.getCU().getOpcode(), components.getGPRRegister().getValue(), components.getCU().getAddress());
        components.CC1.set(components.ALU.CC1);
        components.getGPRRegister().setValue(components.ALU.output, components.ALU.CC1);
    }

    private void executeJZ(int ea) {
        if (components.getGPRRegister().getValue() == 0) {
            components.getPC().setValue(ea);
        }
    }

    private void executeJNE(int ea) {
        if (components.getGPRRegister().getValue() != 0) {
            components.getPC().setValue(ea);
        }
    }

    private void executeJCC(int ea) {
        if (components.getCC().getValue() == 1) {
            components.getPC().setValue(ea);
        }
    }

    private void executeJMA(int ea) {
        components.getPC().setValue(ea);
    }

    private void executeJSR(int ea) {
        components.R3.setValue(components.getPC().getValue());
        components.getPC().setValue(ea);
    }

    private void executeRFS() {
        components.R0.setValue(components.getCU().getAddress());
        components.getPC().setValue(components.R3.getValue());
    }

    private void executeSOB(int ea) {
        components.getGPRRegister().setValue(components.getGPRRegister().getValue() - 1);
        if (components.getGPRRegister().getValue() > 0) {
            components.getPC().setValue(ea);
        }
    }

    private void executeJGE(int ea) {
        if (components.getGPRRegister().getValue() >= 0 && !components.CC1.get()) {
            components.getPC().setValue(ea);
        }
    }

    private void executeMLT() {
        int Rx = components.getRxRegister(true).getValue();
        int Ry = components.getRyRegister(true).getValue();
        components.ALU.Calc(components.getCU().getOpcode(), Rx, Ry);
        Boolean OVERFLOW = components.ALU.CC0;
        int RxIndex = components.getCU().getRx();
        handleMLTResults(RxIndex, OVERFLOW);
    }

    private void executeDVD() {
        int Rx = components.getRxRegister(true).getValue();
        int Ry = components.getRyRegister(true).getValue();
        components.ALU.Calc(components.getCU().getOpcode(), Rx, Ry);
        Boolean DIVZERO = components.ALU.CC2;
        int RxIndex = components.getCU().getRx();
        handleDVDResults(RxIndex, DIVZERO);
    }

    private void executeTRR() {
        int Rx = components.getRxRegister(false).getValue();
        int Ry = components.getRyRegister(false).getValue();
        components.CC3.set(Rx == Ry);
    }

    private void executeAND() {
        int Rx = components.getRxRegister(false).getValue();
        int Ry = components.getRyRegister(false).getValue();
        components.ALU.Calc(components.getCU().getOpcode(), Rx, Ry);
        components.getRxRegister(false).setValue(components.ALU.output);
    }

    private void executeORR() {
        int Rx = components.getRxRegister(false).getValue();
        int Ry = components.getRyRegister(false).getValue();
        components.ALU.Calc(components.getCU().getOpcode(), Rx, Ry);
        components.getRxRegister(false).setValue(components.ALU.output);
    }

    private void executeNOT() {
        int Rx = components.getRxRegister(false).getValue();
        components.ALU.Calc(components.getCU().getOpcode(), Rx, 0);
        components.getRxRegister(false).setValue(components.ALU.output, true);
    }

    private void executeSRC() {
        int Value = components.getGPRRegister().getValue();
        int Count = components.getCU().getCount();
        String StringValue = ToBinaryString(Value, 16);
        StringValue = handleSRC(StringValue, Count);
        components.getGPRRegister().setValue(Integer.parseInt(StringValue, 2));
    }

    private void executeRRC() {
        int Value = components.getGPRRegister().getValue();
        int Count = components.getCU().getCount();
        String StringValue = ToBinaryString(Value, 16);
        StringValue = handleRRC(StringValue, Count);
        components.getGPRRegister().setValue(Integer.parseInt(StringValue, 2));
    }

    private void executeLoadIX(int ea) {
        ea = calculateEA_LDX();
        components.getIXRegister().setValue(dataMemory.get(ea));
    }

    private void executeStoreIX(int ea) {
        dataMemory.set(ea, components.getIXRegister().getValue(), true);
    }

    private void executeIN() {
        int DevID = components.getCU().getAddress();
        char input = handleIN(DevID);
        components.getGPRRegister().setValue((int) input);
    }

    private void executeOUT() {
        int DevID = components.getCU().getAddress();
        handleOUT(DevID);
    }

    // Instruction Handling - Helpers
    private void handleMLTResults(int RxIndex, Boolean OVERFLOW) {
        if (RxIndex == 0) {
            components.R0.setValue(components.ALU.HIResult);
            components.R1.setValue(components.ALU.LOResult);
            components.CC0.set(OVERFLOW);
        } else if (RxIndex == 2) {
            components.R2.setValue(components.ALU.HIResult);
            components.R3.setValue(components.ALU.LOResult);
            components.CC0.set(OVERFLOW);
        } else {
            logInvalidRegisterAccess("MLT", RxIndex);
        }
    }

    private void handleDVDResults(int RxIndex, Boolean DIVZERO) {
        if (RxIndex == 0) {
            components.R0.setValue(components.ALU.HIResult);
            components.R1.setValue(components.ALU.LOResult);
            components.CC2.set(DIVZERO);
        } else if (RxIndex == 2) {
            components.R2.setValue(components.ALU.HIResult);
            components.R3.setValue(components.ALU.LOResult);
            components.CC2.set(DIVZERO);
        } else {
            logInvalidRegisterAccess("DVD", RxIndex);
        }
    }

    private void logInvalidRegisterAccess(String operation, int RxIndex) {
        logging.severe("Request Other than R0 or R2 in Case " + operation);
        System.out.println("Request Other than R0 or R2 in Case " + operation);
    }

    private char handleIN(int DevID) {
        char input = '\n';
        if (DevID == 0) {
            while (KeyboardBuffer.isEmpty()) {
                KeyboardBuffer.setBufferFromGUI();
            }
            input = KeyboardBuffer.getOneDigit();
        } else if (DevID == 2) {
            while (CardReaderBuffer.isEmpty()) {
                CardReaderBuffer.setBufferFromGUI();
            }
            input = CardReaderBuffer.getOneDigit();
        } else if (DevID > 2 && DevID < 32) {
            ConsoleRegisterCollection.setRegisterValue(DevID - 3);
            try {
                input = (char) ConsoleRegisterCollection.getRegisterValue(DevID - 3);
            } catch (Exception e) {
                logging.severe("IN:Cast to char failed.");
                System.out.println("IN:Cast to char failed.");
            }
        } else {
            logging.severe("IN Instr:Invalid DEVID.DEVID>32");
            System.out.println("IN Instr:Invalid DEVID.DEVID>32");
        }
        return input;
    }

    private void handleOUT(int DevID) {
        char output = (char) components.getGPRRegister().getValue();
        if (DevID == 1 || DevID == 2 || (DevID > 2 && DevID < 32)) {
            System.out.print(output);
            OutputString += output;
        } else {
            logging.severe("OUT Instr:Invalid DEVID.DEVID>32");
            System.out.println("OUT Instr:Invalid DEVID.DEVID>32");
        }
    }

    private String handleSRC(String StringValue, int Count) {
        String SignBit = StringValue.substring(0, 1);
        switch (components.getCU().getAL()) {
            case 0 -> { // Arithmetic Shift
                if (components.getCU().getLR() == 0) {
                    if (SignBit.equals("1")) {
                        components.CC1.set(true);
                    }
                    for (int i = 0; i < Count; i++) {
                        StringValue = SignBit + StringValue;
                    }
                    StringValue = StringValue.substring(0, 16);
                } else {
                    for (int i = 0; i < Count; i++) {
                        StringValue = StringValue + "0";
                    }
                    StringValue = SignBit + StringValue.substring(StringValue.length() - 15);
                }
            }
            case 1 -> { // Logical Shift
                if (components.getCU().getLR() == 0) {
                    for (int i = 0; i < Count; i++) {
                        StringValue = "0" + StringValue;
                    }
                    StringValue = StringValue.substring(0, 16);
                } else {
                    for (int i = 0; i < Count; i++) {
                        StringValue = StringValue + "0";
                    }
                    StringValue = StringValue.substring(StringValue.length() - 16);
                }
            }
        }
        return StringValue;
    }

    private String handleRRC(String StringValue, int Count) {
        if (components.getCU().getLR() == 1) {
            String MovePart = StringValue.substring(0, Count);
            StringValue = StringValue + MovePart;
            StringValue = StringValue.substring(StringValue.length() - 16);
        } else {
            String MovePart = StringValue.substring(StringValue.length() - Count);
            StringValue = MovePart + StringValue;
            StringValue = StringValue.substring(0, 16);
        }
        return StringValue;
    }

    public String ToBinaryString(int value, int length) {
        String binaryString = Integer.toBinaryString(value);
        if (binaryString.length() == 32 && binaryString.startsWith("1")) {
            return binaryString.substring(binaryString.length() - length);
        }
        return String.format("%0" + length + "d", Long.parseLong(binaryString));
    }
}
