package TempoObject;

public class Transaksi {
    private JenisTransaksi jenis = JenisTransaksi.MAKAN;
    private Integer hutang = 0;

    public JenisTransaksi getJenis() {
        return jenis;
    }

    public void setJenis(JenisTransaksi jenis) {
        this.jenis = jenis;
    }

    public Integer getHutang() {
        return hutang;
    }

    public void setHutang(Integer hutang) {
        this.hutang = hutang;
    }

    @Override
    public String toString() {
        return "Transaksi [jenis=" + jenis + ", hutang=" + hutang + "]";
    }
}
