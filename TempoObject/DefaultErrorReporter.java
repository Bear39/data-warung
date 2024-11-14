package TempoObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultErrorReporter implements ErrorReporter {
    @Override
    public void reportErrors(List<String> errorPekerja, List<String> errorMandor, String filePath) {
        Path path = Paths.get(filePath);
        Path root = path.getParent().getParent();

        String writtenFilePathString = root.toString() + java.io.File.separator + "revisi" + java.io.File.separator
                + "Comprehensive_Error_Report_"
                + path.getFileName();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writtenFilePathString))) {
            writer.write("COMPREHENSIVE ERROR REPORT");
            writer.newLine();
            writer.write("Generated: " + new Date());
            writer.newLine();
            writer.write("Source File: " + filePath);
            writer.newLine();
            writer.write("Total Errors: " + (errorPekerja.size() + errorMandor.size()));
            writer.newLine();
            writer.write("-------------------------------------------");
            writer.newLine();

            // Categorize and write errors
            Map<String, List<String>> categorizedErrors = 
                Stream.concat(errorPekerja.stream(), errorMandor.stream())
                    .collect(Collectors.groupingBy(
                        error -> error.contains("tempo") ? "Tempo Errors"
                                : error.contains("pekerja") ? "Worker Errors"
                                : error.contains("hutang") ? "Transaction Errors"
                                : "Miscellaneous Errors"));

            for (Map.Entry<String, List<String>> category : categorizedErrors.entrySet()) {
                writer.newLine();
                writer.write(category.getKey() + ":");
                writer.newLine();
                for (String error : category.getValue()) {
                    writer.write("- " + error);
                    writer.newLine();
                }
            }

            System.out.println("Comprehensive error report generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
