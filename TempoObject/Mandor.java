package TempoObject;

import java.util.ArrayList;
import java.util.List;

public class Mandor {
    private String nama = "";
    private List<Tempo> listTempo = new ArrayList<>();

    public List<Tempo> getListTempo() {
        return listTempo;
    }

    public void addTempo(Tempo tempo) {
        this.listTempo.add(tempo);
    }

    public void setListTempo(List<Tempo> listTempo) {
        this.listTempo = listTempo;
    }

    @Override
    public String toString() {
        return "Mandor [listTempo=" + listTempo + "]";
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
