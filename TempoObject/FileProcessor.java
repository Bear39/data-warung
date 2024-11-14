package TempoObject;

import java.util.List;
import java.util.Map;

public interface FileProcessor {
    List<String> readFiles(String folderPath);
    Map<String, Mandor> processFiles(List<String> filenames, String rekapPath);
}
