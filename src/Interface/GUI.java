package Interface;

import CPU.Components;
import Computer.Simulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class GUI extends JFrame {

    // GUI Components
    private JButton IPLButton, SINGLESTEPButton, RUNButton, DMReadButton, DMwriteButton,
            LoadMEMButton, HALTButton, CacheButton, PrintConsoleRegisterButton,
            PCBreakPointAddButton, PCBreakPointRemoveButton;
    private JTextField PCInput, IRInput, MARInput, R0Input, R1Input, R2Input, R3Input,
            IX1Input, IX2Input, IX3Input, MBRInput, MFRInput, DMAddressInput,
            DMValueInput, BreakPointInput;
    private JLabel PC, IR, MAR, R0, R1, R2, R3, IX1, IX2, IX3, MBR, MFR, CC, Address,
            Value, CR;
    private JRadioButton CC0Button, CC1Button, CC2Button, CC3Button;
    private JTextArea Console, Output;
    private JCheckBox ToDECcheckbox, EXPANDMEMCheckbox;
    private JPanel panelMain, OutputField, ConsoleField, BreakPointField;
    private JList BreakPointListView;
    private JTabbedPane tabbedPane1;

    // Simulator instance
    private Simulator simulator;
    private String IOString;

    // Constructor for initializing the GUI components and event listeners
    public GUI() {
        initializeComponents();
        initializeListeners();
    }

    // Initialize GUI components
    private void initializeComponents() {
        simulator = new Simulator();
        SetInputLimiter();
        redirectSystemStreams();
        IOString = "";
        JFileChooser MEMFileChooser = new JFileChooser();
    }

    // Initialize action listeners for GUI components
    private void initializeListeners() {
        // Input field listeners
        addActionListenersToInputFields();

        // Button listeners
        addButtonListeners();

        // Checkbox listeners
        ToDECcheckbox.addActionListener(this::handleToDecimalToggle);
        EXPANDMEMCheckbox.addActionListener(this::handleExpandMemoryToggle);
    }

    // Method to add action listeners to input fields
    private void addActionListenersToInputFields() {
        PCInput.addActionListener(createActionListenerForComponent("PC", PCInput, () -> simulator.components.PC.setValue(PCInput.getText())));
        IRInput.addActionListener(createActionListenerForComponent("IR", IRInput, () -> simulator.components.IR.setValue(IRInput.getText())));
        MARInput.addActionListener(createActionListenerForComponent("MAR", MARInput, () -> simulator.components.MAR.setValue(MARInput.getText())));
        MBRInput.addActionListener(createActionListenerForComponent("MBR", MBRInput, () -> simulator.components.MBR.setValue(MBRInput.getText())));
        MFRInput.addActionListener(createActionListenerForComponent("MFR", MFRInput, () -> simulator.components.MFR.setValue(MFRInput.getText())));
        R0Input.addActionListener(createActionListenerForComponent("R0", R0Input, () -> simulator.components.R0.setValue(R0Input.getText())));
        R1Input.addActionListener(createActionListenerForComponent("R1", R1Input, () -> simulator.components.R1.setValue(R1Input.getText())));
        R2Input.addActionListener(createActionListenerForComponent("R2", R2Input, () -> simulator.components.R2.setValue(R2Input.getText())));
        R3Input.addActionListener(createActionListenerForComponent("R3", R3Input, () -> simulator.components.R3.setValue(R3Input.getText())));
        IX1Input.addActionListener(createActionListenerForComponent("IX1", IX1Input, () -> simulator.components.IX1.setValue(IX1Input.getText())));
        IX2Input.addActionListener(createActionListenerForComponent("IX2", IX2Input, () -> simulator.components.IX2.setValue(IX2Input.getText())));
        IX3Input.addActionListener(createActionListenerForComponent("IX3", IX3Input, () -> simulator.components.IX3.setValue(IX3Input.getText())));
    }

    // Method to add action listeners to buttons
    private void addButtonListeners() {
        DMReadButton.addActionListener(this::handleDMRead);
        DMwriteButton.addActionListener(this::handleDMWrite);
        IPLButton.addActionListener(this::handleIPL);
        RUNButton.addActionListener(this::handleRun);
        HALTButton.addActionListener(this::handleHalt);
        SINGLESTEPButton.addActionListener(this::handleSingleStep);
        LoadMEMButton.addActionListener(this::handleLoadMem);
        CacheButton.addActionListener(e -> simulator.DataMemory.PrintCache());
        PrintConsoleRegisterButton.addActionListener(e -> simulator.BUS.ConsoleRegisterCollection.printCollection());
        PCBreakPointAddButton.addActionListener(this::handleAddBreakpoint);
        PCBreakPointRemoveButton.addActionListener(this::handleRemoveBreakpoint);
    }

    // Helper to create an ActionListener for a specific component
    private ActionListener createActionListenerForComponent(String label, JTextField inputField, Runnable setValueAction) {
        return e -> {
            if (e.getSource() == inputField) {
                setValueAction.run();
                IOString += "\n" + label + "=>" + inputField.getText();
                flushData(simulator.components);
            }
        };
    }

    // Handlers for specific button events
    private void handleDMRead(ActionEvent e) {
        if (ToDECcheckbox.isSelected()) {
            int Memdata = simulator.DataMemory.get(Integer.parseInt(DMAddressInput.getText()));
            DMValueInput.setText(String.valueOf(Memdata));
        } else {
            int Memdata = simulator.DataMemory.get(DMAddressInput.getText());
            DMValueInput.setText(simulator.DataMemory.toBinaryString(Memdata));
        }
        flushData(simulator.components);
    }

    private void handleDMWrite(ActionEvent e) {
        boolean isBinary = !ToDECcheckbox.isSelected();
        simulator.DataMemory.UserSet(DMAddressInput.getText(), DMValueInput.getText(), isBinary);
        IOString += "\n" + "MEM[" + DMAddressInput.getText() + "]=>" + DMValueInput.getText();
        flushData(simulator.components);
    }

    private void handleIPL(ActionEvent e) {
        simulator = new Simulator();
        IOString = "";
        flushData(simulator.components);
        DMValueInput.setText("0000000000000000");
        DMAddressInput.setText("000000000000");
    }

    private void handleRun(ActionEvent e) {
        simulator.BUS.run();
        flushData(simulator.components);
    }

    private void handleHalt(ActionEvent e) {
        simulator.BUS.setHalt();
        flushData(simulator.components);
    }

    private void handleSingleStep(ActionEvent e) {
        simulator.BUS.tik();
        flushData(simulator.components);
    }

    private void handleLoadMem(ActionEvent e) {
        JFileChooser MEMFileChooser = new JFileChooser();
        int select = MEMFileChooser.showOpenDialog(null);
        if (select == MEMFileChooser.APPROVE_OPTION) {
            File MEMFile = MEMFileChooser.getSelectedFile();
            System.out.println("[MEMLOAD]MAPPING MEM FROM FILE:" + MEMFile.getName());
            simulator.loadMEMfromFile(MEMFile);
        }
    }

    private void handleToDecimalToggle(ActionEvent e) {
        if (ToDECcheckbox.isSelected()) {
            SetInputLimiterDEC();
            flushDataDEC(simulator.components);
        } else {
            SetInputLimiter();
            flushDataBIN(simulator.components);
        }
    }

    private void handleExpandMemoryToggle(ActionEvent e) {
        if (EXPANDMEMCheckbox.isSelected()) simulator.DataMemory.expandMEM();
        else simulator.DataMemory.shrinkMEM();
    }

    private void handleAddBreakpoint(ActionEvent e) {
        int BreakPointPC = Integer.parseInt(BreakPointInput.getText());
        simulator.BUS.setBreakPoint(BreakPointPC);
        BreakPointListView.setListData(simulator.BUS.getBreakPointList());
    }

    private void handleRemoveBreakpoint(ActionEvent e) {
        int BreakPointPC = Integer.parseInt(BreakPointInput.getText());
        simulator.BUS.removeBreakPoint(BreakPointPC);
        BreakPointListView.setListData(simulator.BUS.getBreakPointList());
    }

    // Flush GUI with data in binary or decimal format based on mode
    public void flushData(Components data) {
        if (ToDECcheckbox.isSelected()) flushDataDEC(data);
        else flushDataBIN(data);
    }

    private void flushDataBIN(Components data) {
        redirectSystemStreams();
        updateFieldsWithBinary(data);
        Output.setText(simulator.BUS.OutputString);
    }

    private void flushDataDEC(Components data) {
        redirectSystemStreams();
        updateFieldsWithDecimal(data);
        Output.setText(simulator.BUS.OutputString);
    }

    // Helper methods to update fields with data in binary or decimal format
    private void updateFieldsWithBinary(Components data) {
        PCInput.setText(data.PC.ToBinaryString());
        IRInput.setText(data.IR.ToBinaryString());
        MARInput.setText(data.MAR.ToBinaryString());
        MBRInput.setText(data.MBR.ToBinaryString());
        MFRInput.setText(data.MFR.ToBinaryString());
        R0Input.setText(data.R0.ToBinaryString());
        R1Input.setText(data.R1.ToBinaryString());
        R2Input.setText(data.R2.ToBinaryString());
        R3Input.setText(data.R3.ToBinaryString());
        IX1Input.setText(data.IX1.ToBinaryString());
        IX2Input.setText(data.IX2.ToBinaryString());
        IX3Input.setText(data.IX3.ToBinaryString());
        updateConditionCodes(data);
    }

    private void updateFieldsWithDecimal(Components data) {
        PCInput.setText("" + data.PC.getValue());
        IRInput.setText("" + data.IR.getValue());
        MARInput.setText("" + data.MAR.getValue());
        MBRInput.setText("" + data.MBR.getValue());
        MFRInput.setText("" + data.MFR.getValue());
        R0Input.setText("" + data.R0.getValue());
        R1Input.setText("" + data.R1.getValue());
        R2Input.setText("" + data.R2.getValue());
        R3Input.setText("" + data.R3.getValue());
        IX1Input.setText("" + data.IX1.getValue());
        IX2Input.setText("" + data.IX2.getValue());
        IX3Input.setText("" + data.IX3.getValue());
        updateConditionCodes(data);
    }

    private void updateConditionCodes(Components data) {
        CC0Button.setSelected(data.CC0.get());
        CC1Button.setSelected(data.CC1.get());
        CC2Button.setSelected(data.CC2.get());
        CC3Button.setSelected(data.CC3.get());
    }

    // Input limitation and console redirection methods
    private void SetInputLimiterDEC() {
        LimitedDocument PCInputLimiter = new LimitedDocument(4);
        PCInputLimiter.setAllowChar("1234567890");
        PCInput.setDocument(PCInputLimiter);
    }

    private void SetInputLimiter() {
        LimitedDocument PCInputLimiter = new LimitedDocument(12);
        PCInputLimiter.setAllowChar("01");
        PCInput.setDocument(PCInputLimiter);
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(() -> Console.append(text));
    }

    // Main method to run the GUI
    public static void main(String args[]) {
        JFrame jFrame = new JFrame("CSCI6461 Computer Simulator Team 10");
        GUI gui = new GUI();
        jFrame.setContentPane(gui.panelMain);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
