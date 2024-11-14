package TempoObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public interface OutputWriter {
    void writeOutput(BufferedWriter fWriter, Mandor curMandor, String tanggalMulai, 
                     String tanggalAkhir, int totalPayment, 
                     TreeMap<String, Integer> totalHutangTiapPekerja) throws IOException;
}
