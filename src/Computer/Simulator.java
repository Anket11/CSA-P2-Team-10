package Computer;

import CPU.Components;
import Interface.IOBuffer;
import Memory.Memory;
import CPU.Bus;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;

public class Simulator {

    public Components components;
    public Memory DataMemory;
    public Bus BUS;

    public Simulator() {
        initialize();
    }

    public void initialize() {
        components = new Components();
        DataMemory = new Memory();
        BUS = new Bus(components,DataMemory);
    }

    public void loadMEMfromFile(File file) {
        try {
            int i = 0;
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] buff = line.split(",");
                DataMemory.UserSet(buff[0], buff[1], true);
                i++;
            }
            System.out.println("[MEMLOAD]SET " + i + " MEMORY DATA TOTAL.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


