package Interface;

import CPU.Register;
import javax.swing.*;
import java.util.logging.Logger;

public class ConsoleRegisterCollection {

    // Collection of ConsoleRegisters
    public ConsoleRegister[] Collections = new ConsoleRegister[29];

    // Constructor to initialize ConsoleRegisterCollection
    public ConsoleRegisterCollection() {
        initializeRegisters();
    }

    // Initializes ConsoleRegister objects in the collection
    private void initializeRegisters() {
        for (int i = 0; i < Collections.length; i++) {
            Collections[i] = new ConsoleRegister(0);
        }
    }

    public int getRegisterValue(int index) {
        return Collections[index].getValue();
    }

    public void setRegisterValue(int index) {
        Collections[index].setConsoleRegisterFromGUI();
    }

    public void printCollection() {
        for (int i = 0; i < Collections.length; i++) {
            logAndPrintRegister(i);
        }
    }

    // Logs and prints a specific ConsoleRegister
    private void logAndPrintRegister(int index) {

        System.out.println("DUMP:CR:" + index + "=>" + Collections[index].ToBinaryString() + "(" + Collections[index].getValue() + ")");
    }
}

class ConsoleRegister extends Register {

    // Constructor for ConsoleRegister with specified value
    public ConsoleRegister(int value) {super(value, 16, "ConsoleRegister");
    }

    public void setConsoleRegisterFromGUI() {
        String input = getUserInput();
        if (input != null && !input.isEmpty()) {
            setValue((int) input.charAt(0));
        }
    }

    // Opens an input dialog to get user input
    private String getUserInput() {
        JFrame frame = new JFrame("Please provide an Input!");
        return JOptionPane.showInputDialog(frame, "Input for ConsoleRegister");
    }
}
