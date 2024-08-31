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
