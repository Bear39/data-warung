package TempoObject;

import java.util.Comparator;

public class PekerjaComparator implements Comparator<Pekerja> {
    @Override
    public int compare(Pekerja obj1, Pekerja obj2) {
        return obj1.getNama().compareTo(obj2.getNama());
    }
}
