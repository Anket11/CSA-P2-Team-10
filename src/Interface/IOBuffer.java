package Interface;

import javax.swing.*;

public class IOBuffer {
    private String Buffer;
    private String DeviceName;

    public IOBuffer(String DeviceName) {
        this.DeviceName = DeviceName;
        this.Buffer = "";
    }

    public void setBuffer(String input) {
        this.Buffer = input;
    }

    public void setBufferFromGUI() {
        JFrame frame = new JFrame(DeviceName + "Please provide your Input");
        String input = JOptionPane.showInputDialog(frame, "Please type in your input for " + DeviceName);
        input = input + "\n";
        setBuffer(input);
    }


    public char getOneDigit() {
        char ret = Buffer.charAt(0);
        Buffer = Buffer.substring(1);
        return ret;
    }

    public boolean isEmpty() {
        return Buffer.length() == 0;
    }
}
