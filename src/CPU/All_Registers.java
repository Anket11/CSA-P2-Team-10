package CPU;

public class All_Registers {
    public static class Address_Register extends Register {
        public Address_Register(int value) {
            super(value, 16, "EA");
        }
    }
    public static class General_Purpose_Registers extends Register {
        public General_Purpose_Registers(int value) {
            super(value, 16, "GPR");
        }
    }
    public static class Instruction_Register extends Register {
        public Instruction_Register(int value) {
            super(value, 16, "IR");
        }
    }
    public static class Machine_Fault_Register extends Register {
        public Machine_Fault_Register(int value) {
            super(value, 16, "MF");
        }
    }
    public static class IX_Register extends Register {
        public IX_Register(int value) {super(value, 16, "IX"); }
    }
    public static class Memory_Address_Register extends Register {
        public Memory_Address_Register(int value) {
            super(value, 16, "MAR");
        }
    }
    public static class Memory_Buffer_Register extends Register {
        public Memory_Buffer_Register(int value) {
            super(value, 16, "MBR");
        }
    }
    public static class ProgramCounter extends Register {
        public ProgramCounter(int value) {
            super(value, 12, "PC");
        }

        public void incrementOne() {
            int value = getValue() + 1;
            this.setValue(value);
        }
    }
}
