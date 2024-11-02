package CPU;
public class Components {

    // Condition Code Registers
    public Condition_Code CC0, CC1, CC2, CC3;

    // Memory Address Register
    public All_Registers.Memory_Address_Register MAR;

    // Memory Buffer Register
    public All_Registers.Memory_Buffer_Register MBR;

    // Effective Address Register (EA)
    public All_Registers.Address_Register EA;

    // Program Counter
    public All_Registers.ProgramCounter PC;

    // Machine Fault Register
    public All_Registers.Machine_Fault_Register MFR;

    // General Purpose Registers
    public All_Registers.General_Purpose_Registers R0, R1, R2, R3;

    // Instruction Register
    public All_Registers.Instruction_Register IR;

    // Index Registers
    public All_Registers.IX_Register IX1, IX2, IX3;
    // Arithmetic Logic Unit
    public ALU ALU;

    // Control Unit
    private ControlUnit CU;

    // Logger
//    final Logger logging = Logger.getLogger("CPU.RegistersCollection");

    // Constructor
    public Components() {
        initialize();
    }

    public void initialize() {
        // Initialize Condition Code Registers
        CC0 = new Condition_Code(false);
        CC1 = new Condition_Code(false);
        CC2 = new Condition_Code(false);
        CC3 = new Condition_Code(false);



        // Initialize Instruction Register
        IR = new All_Registers.Instruction_Register(0);

        // Initialize Index Registers
        IX1 = new All_Registers.IX_Register(0);
        IX2 = new All_Registers.IX_Register(0);
        IX3 = new All_Registers.IX_Register(0);

        // Initialize Machine Fault Register
        MFR = new All_Registers.Machine_Fault_Register(0);

        // Initialize Memory Address and Buffer Registers
        MAR = new All_Registers.Memory_Address_Register(0);
        MBR = new All_Registers.Memory_Buffer_Register(0);

        // Initialize General Purpose Registers
        R0 = new All_Registers.General_Purpose_Registers(0);
        R1 = new All_Registers.General_Purpose_Registers(0);
        R2 = new All_Registers.General_Purpose_Registers(0);
        R3 = new All_Registers.General_Purpose_Registers(0);

        // Initialize Effective Address Register
        EA = new All_Registers.Address_Register(0);

        // Initialize Program Counter
        PC = new All_Registers.ProgramCounter(6);

        // Initialize Arithmetic Logic Unit and Control Unit
        ALU = new ALU();
        CU = new ControlUnit(IR.ToBinaryString());
    }

    public Register getCC() {
        switch (CU.getR()) {
            case 0: return CC0;
            case 1: return CC1;
            case 2: return CC2;
            default: return CC3;
        }
    }

    public Register getGPRRegister() {
        switch (CU.getR()) {
            case 0: return R0;
            case 1: return R1;
            case 2: return R2;
            default: return R3;
        }
    }

    public Register getIXRegister() {
        switch (CU.getIX()) {
            case 1: return IX1;
            case 2: return IX2;
            case 3: return IX3;
            default:
                System.out.println("outPut 0 return an invalid IX0 Register");
                return new Register(0, 0, "");
        }
    }

    public Register getRxRegister(boolean limited) {
        int index = CU.getRx();
        if (index == 0) return R0;
        if (index == 2) return R2;
        if (!limited) {
            if (index == 1) return R1;
            if (index == 3) return R3;
        }
        System.out.println("Rx Register call");
        return new Register(0, 0, "");
    }

    public Register getRyRegister(boolean limited) {
        int index = CU.getRy();
        if (index == 0) return R0;
        if (index == 2) return R2;
        if (!limited) {
            if (index == 1) return R1;
            if (index == 3) return R3;
        }
        System.out.println("RY Register call");
        return new Register(0, 0, "");
    }

    // Methods for retrieving specific registers and control unit

    public Register getMAR() {
        return MAR;
    }

    public ControlUnit getCU() {
        return CU;
    }
    public Register getAddressRegister() {
        return EA;
    }

    public All_Registers.ProgramCounter getPC() {
        return PC;
    }
    public Register getMBR() {
        return MBR;
    }

    public Register getIR() {
        return IR;
    }
}
