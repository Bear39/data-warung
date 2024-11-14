package TempoObject;

import TempoObject.model.TransactionType;

public class Transaksi {
    private TransactionType type = TransactionType.NORMAL;
    private Integer hutang = 0;

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Integer getHutang() {
        return hutang;
    }

    public void setHutang(Integer hutang) {
        this.hutang = hutang;
    }

    public String getFormattedHutang() {
        return hutang + type.getSuffix();
    }

    @Override
    public String toString() {
        return "Transaksi [type=" + type + ", hutang=" + hutang + "]";
    }
}
