package TempoObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DefaultFileProcessor implements FileProcessor {
    private SimpleDateFormat dateFormat;
    private ErrorReporter errorReporter;

    public DefaultFileProcessor(ErrorReporter errorReporter) {
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy");
        this.errorReporter = errorReporter;
    }

    @Override
    public List<String> readFiles(String folderPath) {
        List<String> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    result.add(file.getFileName().toString());
                }
            }
            return result;
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Mandor> processFiles(List<String> filenames, String rekapPath) {
        Map<String, Mandor> mapMandor = new HashMap<>();

        for (String fileName : filenames) {
            List<String> errorMandor = new ArrayList<>();
            List<String> errorPekerja = new ArrayList<>();
            String mandorName = fileName.split(" - ")[0];
            Mandor mandor = mapMandor.getOrDefault(mandorName, new Mandor());
            mandor.setNama(mandorName);

            String filePath = rekapPath + java.io.File.separator + fileName;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
                // Existing file processing logic from checkTempo method
                // ... (copy the existing file processing logic here)
                // Make sure to use the existing methods like containsNonAlphabetic, etc.

                // Report errors if any
                if (!errorPekerja.isEmpty() || !errorMandor.isEmpty()) {
                    errorReporter.reportErrors(errorPekerja, errorMandor, filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error processing file " + fileName);
            }

            mapMandor.put(mandorName, mandor);
        }

        return mapMandor;
    }
}
