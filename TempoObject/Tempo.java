package TempoObject;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class Tempo {
    private String namaTempo = "";
    private Date waktuTempo = null;
    private Set<Pekerja> setPekerja = new TreeSet<>(new PekerjaComparator());
    private Boolean totalTempoExist = false;
    private Integer totalTempo = 0;
    private Boolean hasError = false;
    private String errorMessage = "";

    // New method to validate total tempo against worker totals
    public boolean validateTotalTempo() {
        int calculatedTotal = setPekerja.stream()
            .mapToInt(Pekerja::getTotalUtang)
            .sum();
        
        // If total tempo was not originally specified, set it to calculated total
        if (!totalTempoExist) {
            totalTempo = calculatedTotal;
            totalTempoExist = true;
            return true;
        }
        
        // Check if calculated total matches specified total
        return calculatedTotal == totalTempo;
    }

    // Method to get total hutang for a specific transaction type
    public Integer getTotalHutangByType(JenisTransaksi jenis) {
        return setPekerja.stream()
            .mapToInt(pekerja -> 
                pekerja.getListTransaksi().stream()
                    .filter(t -> t.getJenis() == jenis)
                    .mapToInt(Transaksi::getHutang)
                    .sum()
            )
            .sum();
    }

    public String getNamaTempo() {
        return namaTempo;
    }

    public void setNamaTempo(String namaTempo) {
        this.namaTempo = namaTempo;
    }

    public Set<Pekerja> getSetPekerja() {
        return setPekerja;
    }

    public Boolean addPekerja(Pekerja pekerja) {
        return this.setPekerja.add(pekerja);
    }

    public Boolean hasTotalTempo() {
        return totalTempoExist;
    }

    public void setTotalTempoExistence(Boolean totalTempoExistence) {
        this.totalTempoExist = totalTempoExistence;
    }

    public Integer getTotalTempo() {
        return totalTempo;
    }

    public void setTotalTempo(Integer totalTempo) {
        this.totalTempo = totalTempo;
    }

    @Override
    public String toString() {
        return "Tempo [namaTempo=" + namaTempo + ", setPekerja=" + setPekerja + ", hasTotalTempo=" + totalTempoExist
                + ", totalTempo=" + totalTempo + "]";
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getWaktuTempo() {
        return waktuTempo;
    }

    public void setWaktuTempo(Date waktuTempo) {
        this.waktuTempo = waktuTempo;
    }
}
