package TempoObject;

import java.util.Comparator;

public class TempoComparator implements Comparator<Tempo> {
    @Override
    public int compare(Tempo obj1, Tempo obj2) {
        return obj1.getNamaTempo().compareTo(obj2.getNamaTempo());
    }
}
