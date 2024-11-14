package TempoObject;

import java.util.List;

public interface ErrorReporter {
    void reportErrors(List<String> errorPekerja, List<String> errorMandor, String filePath);
}
