package TempoObject;

import java.util.ArrayList;
import java.util.List;

public class Pekerja {
    private String nama = "";
    private List<Transaksi> listTransaksi = new ArrayList<>();
    private Integer totalUtang = 0;
    private Boolean hasError = false;
    private String errorMessage = "";

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public List<Transaksi> getListTransaksi() {
        return listTransaksi;
    }

    public void addTransaksi(Transaksi transaksi) {
        this.listTransaksi.add(transaksi);
    }

    public Integer getTotalUtang() {
        return totalUtang;
    }

    public void setTotalUtang(Integer totalUtang) {
        this.totalUtang = totalUtang;
    }

    @Override
    public String toString() {
        return "Pekerja [nama=" + nama + ", listTransaksi=" + listTransaksi + ", totalUtang=" + totalUtang + "]";
    }

    public Boolean getHasError() {
        return hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
