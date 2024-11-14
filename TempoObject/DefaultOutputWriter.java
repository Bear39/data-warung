package TempoObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class DefaultOutputWriter implements OutputWriter {
    @Override
    public void writeOutput(BufferedWriter fWriter, Mandor curMandor, String tanggalMulai, 
                             String tanggalAkhir, int totalPayment, 
                             TreeMap<String, Integer> totalHutangTiapPekerja) throws IOException {
        fWriter.write("NAMA: " + curMandor.getNama().toUpperCase());
        fWriter.newLine();
        fWriter.write("PERIODE: " + tanggalMulai.toUpperCase() + " - " + tanggalAkhir.toUpperCase());
        fWriter.newLine();
        fWriter.write("TOTAL: " + totalPayment);
        fWriter.newLine();
        fWriter.write("DETAIL:");
        fWriter.newLine();
        for (Map.Entry<String, Integer> entry : totalHutangTiapPekerja.entrySet()) {
            fWriter.write("- " + entry.getKey() + ": " + entry.getValue());
            fWriter.newLine();
        }
        fWriter.newLine();
    }
}
