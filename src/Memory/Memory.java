package Memory;

import java.util.Vector;

/**
 * This is the Memory and Cache class.
 */
public class Memory {
    public static class MemoryData {
        int address;
        int value;

        public MemoryData(int address, int value) {
            this.address = address;
            this.value = value;
        }
    }
    // Constants and Variables
    private final static int WORD_LENGTH = 16;
    private int MEMORY_LENGTH = 2048;
    public MemoryData[] Memory;
    public static Vector<MemoryData> cache = new Vector<>(16);

    // Constructor
    public Memory() {
        Memory = new MemoryData[MEMORY_LENGTH];
    }

    // Memory Management
    public void expandMEM() {
        MEMORY_LENGTH = 4096;
        Memory = new MemoryData[MEMORY_LENGTH];
    }

    public void shrinkMEM() {
        MEMORY_LENGTH = 2048;
        Memory = new MemoryData[MEMORY_LENGTH];
    }


    // Cache Management
    public void addElementtoCache(MemoryData newData) {
        cache.add(0, newData);
        cache.setSize(16);
    }

    // Data Retrieval
    public int get(String address) {
        int intAddress = Integer.valueOf(address, 2);
        return get(intAddress);
    }

    public int get(int address) {
        for (MemoryData current : cache) {
            if (current.address == address) {

                return current.value;
            }
        }

        for (MemoryData current : Memory) {
            if (current != null && current.address == address) {
                return current.value;
            }
        }

        return 0;
    }


    public void UserSet(String address, String value, boolean isBinary) {
        int intAddress = isBinary ? Integer.valueOf(address, 2) : Integer.valueOf(address);
        int intValue = isBinary ? Integer.valueOf(value, 2) : Integer.valueOf(value);

        if (intAddress < 6) {
            logAndPrint("User attempted to set protected memory.");
        } else {
            set(intAddress, intValue);
        }
    }

    public void set(int address, int value, boolean userSet) {
        if (userSet && address < 6) {
            logAndPrint("User attempted to set protected memory.");
        } else {
            set(address, value);
        }
    }

    public void set(int address, int value) {
        if (address > Memory.length || value > 65536) {
            logAndPrint("Invalid address or value: MEM");
        } else {
            MemoryData current = new MemoryData(address, value);
            addElementtoCache(current);
            Memory[address] = current;
        }
    }

    // Utility Methods
    public String toBinaryString(int value) {
        String binaryString = Integer.toBinaryString(value);
        return String.format("%016d", Long.valueOf(binaryString));
    }

    private void logAndPrint(String message) {
        System.out.println(message);
    }

    // Data Dumping Methods
    public void PrintCache() {
        for (int i = 0; i < cache.size(); i++) {
            MemoryData data = cache.get(i);
            logAndPrint("CACHE #" + i + " [" + toBinaryString(data.address) + "(" + data.address + ")]=>" + toBinaryString(data.value) + "(" + data.value + ")");
        }
        logAndPrint("TOTAL DUMPED " + cache.size() + " CACHE LOGS.");
    }

}
