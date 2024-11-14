package TempoObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TempoHelper {

    private static List<InputObject> getInput(Access akses) {

        String dirPath = getAccessPath(akses);

        String fileInput = dirPath + File.separator + "INPUT";

        List<InputObject> inputObjects = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileInput))) {
            String line;
            InputObject currentObject = null;

            while ((line = br.readLine()) != null) {
                // Check if the line is a name (i.e., does not contain a date range)
                if (!line.contains(" - ")) {
                    // If there's a previous InputObject, add it to the list
                    if (currentObject != null) {
                        inputObjects.add(currentObject);
                    }

                    // Create a new InputObject with the current name
                    currentObject = new InputObject();
                    currentObject.setInputName(line);
                } else if (currentObject != null) { // Process date ranges
                    String[] dates = line.split(" - ");
                    if (dates.length == 2) {
                        InputDate inputDate = new InputDate();
                        inputDate.setStartDate(dates[0].trim());
                        inputDate.setEndDate(dates[1].trim());

                        // Add the InputDate to the current InputObject
                        currentObject.addInputDate(inputDate);
                    }
                }
            }

            // Add the last InputObject after the loop
            if (currentObject != null) {
                inputObjects.add(currentObject);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputObjects;
    }

    private static List<String> readFiles(String folderPath) {
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
        }
        return null;
    }

    private static TreeMap<String, Integer> calculateEachWorker(Mandor mandor, Date start, Date end) {
        TreeMap<String, Integer> mapResult = new TreeMap<>();

        for (Tempo tempo : mandor.getListTempo()) {
            Date tanggalTempo = tempo.getWaktuTempo();
            if (isWithinRange(tanggalTempo, start, end)) {
                for (Pekerja pekerja : tempo.getSetPekerja()) {
                    mapResult.put(pekerja.getNama(),
                            mapResult.getOrDefault(pekerja.getNama(), 0) + pekerja.getTotalUtang());
                }
            }
        }
        return mapResult;
    }

    private static int calculateTotalPayment(Mandor mandor, Date start, Date end) {
        return mandor.getListTempo().stream().filter(tempo -> isWithinRange(tempo.getWaktuTempo(), start, end))
                .mapToInt(Tempo::getTotalTempo).sum();
    }

    private static Boolean isWithinRange(Date current, Date start, Date end) {
        return current.compareTo(start) >= 0 && current.compareTo(end) <= 0;
    }

    private static void writeSummary(Mandor mandor, String filePath) {
        Path path = Paths.get(filePath);
        Path root = path.getParent().getParent();

        String writtenFilePathString = root.toString() + File.separator + "csv" + File.separator
                + "SUMMARY_" + path.getFileName();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writtenFilePathString))) {
            // Detailed summary with transaction type breakdown
            for (Tempo dataTempo : mandor.getListTempo()) {
                writer.write("Date: " + dataTempo.getWaktuTempo().toString());
                writer.newLine();
                writer.write("Total Tempo: " + dataTempo.getTotalTempo().toString());
                writer.newLine();
            
                // Transaction type summary
                writer.write("Total Makan: " + dataTempo.getTotalHutangByType(JenisTransaksi.MAKAN));
                writer.newLine();
                writer.write("Total Rokok: " + dataTempo.getTotalHutangByType(JenisTransaksi.ROKOK));
                writer.newLine();
                writer.write("Total Wallet: " + dataTempo.getTotalHutangByType(JenisTransaksi.WALLET));
                writer.newLine();
                writer.newLine();
            }
        
            // Monthly aggregation with transaction type breakdown
            Map<String, Map<JenisTransaksi, Integer>> monthlyDetailedMap = mandor.getListTempo().stream()
                .collect(
                    Collectors.groupingBy(
                        t -> {
                            LocalDate localDate = t.getWaktuTempo().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return localDate.getMonthValue() + "-" + localDate.getYear();
                        },
                        Collectors.groupingBy(
                            t -> JenisTransaksi.MAKAN,
                            Collectors.summingInt(t -> t.getTotalHutangByType(JenisTransaksi.MAKAN))
                        )
                    )
                );

            writer.write("Monthly Detailed Summary:");
            writer.newLine();
            monthlyDetailedMap.forEach((monthYear, typeMap) -> {
                try {
                    writer.write("Month-Year: " + monthYear);
                    writer.newLine();
                    typeMap.forEach((type, amount) -> {
                        try {
                            writer.write(type + ": " + amount);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeValidatedCSV(Mandor mandor, String filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            for (Tempo tempo : mandor.getListTempo()) {
                // Comprehensive validation for each tempo
                boolean tempoValid = tempo.validateTotalTempo();
                
                bufferedWriter.write(tempo.getNamaTempo() + "," + tempo.getTotalTempo().toString());
                if (!tempoValid || tempo.getHasError()) {
                    bufferedWriter.write(",VALIDATION_ERROR");
                    bufferedWriter.write("," + (tempo.getErrorMessage() != null ? tempo.getErrorMessage() : "Total Tempo Mismatch"));
                }
                bufferedWriter.newLine();
                
                for (Pekerja pekerja : tempo.getSetPekerja()) {
                    // Detailed worker validation
                    boolean pekerjaValid = pekerja.validateTotalHutang();
                    
                    bufferedWriter.write(pekerja.getNama() + ",");
                    
                    // Detailed transaction breakdown
                    bufferedWriter.write("Makan: " + pekerja.getTotalMakanHutang() + ",");
                    bufferedWriter.write("Rokok: " + pekerja.getTotalRokokHutang() + ",");
                    bufferedWriter.write("Wallet: " + pekerja.getTotalWalletHutang() + ",");
                    
                    bufferedWriter.write("Total: " + pekerja.getTotalUtang());
                    
                    if (!pekerjaValid || pekerja.getHasError()) {
                        bufferedWriter.write(",VALIDATION_ERROR");
                        bufferedWriter.write("," + (pekerja.getErrorMessage() != null ? pekerja.getErrorMessage() : "Total Hutang Mismatch"));
                    }
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine(); // Separate each tempo
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing validated CSV");
        }
    }

    private static void writeCsv(Mandor mandor, String filePath) {
        Path path = Paths.get(filePath);
        Path root = path.getParent().getParent();

        String writtenFilePathString = root.toString() + File.separator + "csv" + File.separator
                + path.getFileName();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writtenFilePathString))) {
            // Iterate through the List and write each element to the file with a new line
            for (Tempo dataTempo : mandor.getListTempo()) {
                for (Pekerja dataPekerja : dataTempo.getSetPekerja()) {
                    List<Transaksi> transaksiMakan = dataPekerja.getListTransaksi().stream()
                            .filter(data -> data.getJenis() == JenisTransaksi.MAKAN)
                            .collect(Collectors.toList());
                    writer.write(dataTempo.getNamaTempo());
                    writer.write(",");
                    writer.write(dataPekerja.getNama());
                    writer.write(",");
                    writer.write("Makan");
                    writer.write(",");
                    for (Transaksi trans : transaksiMakan) {
                        int dataHutang = trans.getHutang();
                        writer.write("" + dataHutang);
                        writer.write(",");
                    }
                    for (int i = transaksiMakan.size(); i <= 10; i++) {
                        writer.write(",");
                    }
                    int totalMakan = transaksiMakan.stream().mapToInt(Transaksi::getHutang).sum();
                    writer.write("" + totalMakan);
                    writer.newLine();
                    List<Transaksi> transaksiRokok = dataPekerja.getListTransaksi().stream()
                            .filter(data -> data.getJenis() == JenisTransaksi.ROKOK)
                            .collect(Collectors.toList());
                    writer.write(dataTempo.getNamaTempo());
                    writer.write(",");
                    writer.write(dataPekerja.getNama());
                    writer.write(",");
                    writer.write("Rokok");
                    writer.write(",");
                    for (Transaksi trans : transaksiRokok) {
                        int dataHutang = trans.getHutang();
                        writer.write("" + dataHutang);
                        writer.write(",");
                    }
                    for (int i = transaksiRokok.size(); i <= 10; i++) {
                        writer.write(",");
                    }
                    int totalRokok = transaksiRokok.stream().mapToInt(Transaksi::getHutang).sum();
                    writer.write("" + totalRokok);
                    writer.newLine();
                    List<Transaksi> transaksiWallet = dataPekerja.getListTransaksi().stream()
                            .filter(data -> data.getJenis() == JenisTransaksi.WALLET)
                            .collect(Collectors.toList());
                    writer.write(dataTempo.getNamaTempo());
                    writer.write(",");
                    writer.write(dataPekerja.getNama());
                    writer.write(",");
                    writer.write("Wallet");
                    writer.write(",");
                    for (Transaksi trans : transaksiWallet) {
                        int dataHutang = trans.getHutang();
                        writer.write("" + dataHutang);
                        writer.write(",");
                    }
                    for (int i = transaksiRokok.size(); i <= 10; i++) {
                        writer.write(",");
                    }
                    int totalWallet = transaksiWallet.stream().mapToInt(Transaksi::getHutang).sum();
                    writer.write("" + totalWallet);
                    writer.newLine();
                }
            }
            System.out.println("Data has been written to the file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // New method for generating transaction type summary
    private static void generateTransactionTypeSummary(List<Mandor> mandors, 
                                                       JenisTransaksi transactionType) {
        Path summaryPath = Paths.get(getAccessPath(Access.BIASA) + File.separator + "summary");
        
        Date startDate;
        Date endDate;

        try {
            startDate = new SimpleDateFormat("dd MMM yyyy").parse("01 OCT 2023");
            endDate = new SimpleDateFormat("dd MMM yyyy").parse("01 JAN 2025");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Ensure summary directory exists
        try {
            Files.createDirectories(summaryPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy");
        String summaryFileName = String.format("SUMMARY_%s - %s.csv", 
                                               transactionType.name(), 
                                               monthYearFormat.format(startDate));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryPath.resolve(summaryFileName).toFile()))) {
            // CSV Header
            writer.write("Date,Total Amount");
            writer.newLine();

            // Filter and aggregate transactions by type and date range
            mandors.stream()
                .flatMap(mandor -> mandor.getListTempo().stream())
                .filter(tempo -> isWithinRange(tempo.getWaktuTempo(), startDate, endDate))
                .collect(Collectors.groupingBy(
                    Tempo::getWaktuTempo,
                    Collectors.summingInt(tempo -> 
                        tempo.getSetPekerja().stream()
                            .flatMap(pekerja -> pekerja.getListTransaksi().stream())
                            .filter(transaksi -> transaksi.getJenis() == transactionType)
                            .mapToInt(Transaksi::getHutang)
                            .sum()
                    )
                ))
                .forEach((date, totalAmount) -> {
                    try {
                        writer.write(String.format("%s,%d", new SimpleDateFormat("dd MMM yyyy").format(date), totalAmount));
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // New method for generating daily mandor total summary
    private static void generateMandorDailySummary(List<Mandor> mandors) {
        Path summaryPath = Paths.get(getAccessPath(Access.BIASA) + File.separator + "summary");
        
        Date startDate;
        Date endDate;

        try {
            startDate = new SimpleDateFormat("dd MMM yyyy").parse("01 OCT 2023");
            endDate = new SimpleDateFormat("dd MMM yyyy").parse("01 JAN 2025");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
            
        try {
            Files.createDirectories(summaryPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy");
        String summaryFileName = String.format("SUMMARY - %s.csv", monthYearFormat.format(startDate));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryPath.resolve(summaryFileName).toFile()))) {
            // CSV Header
            writer.write("Date,Total Kasbon,");
            mandors.forEach(mandor -> {
                try {
                    writer.write(mandor.getNama() + " Total,");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.newLine();

            // Group and aggregate daily totals
            mandors.stream()
                .flatMap(mandor -> mandor.getListTempo().stream())
                .filter(tempo -> isWithinRange(tempo.getWaktuTempo(), startDate, endDate))
                .collect(Collectors.groupingBy(
                    Tempo::getWaktuTempo,
                    Collectors.toList()
                ))
                .forEach((date, tempos) -> {
                    try {
                        int totalKasbon = tempos.stream().mapToInt(Tempo::getTotalTempo).sum();
                        writer.write(new SimpleDateFormat("dd MMM yyyy").format(date) + "," + totalKasbon + ",");
                        
                        for (Mandor mandor : mandors) {
                            int mandorTotal = tempos.stream()
                                .filter(tempo -> tempo.getNamaTempo().startsWith(mandor.getNama()))
                                .mapToInt(Tempo::getTotalTempo)
                                .sum();
                            writer.write(mandorTotal + ",");
                        }
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteContents(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Recursively delete subdirectories
                        deleteContents(file);
                    } else {
                        // Delete file
                        file.delete();
                    }
                }
            }
        } else {
            System.out.println("Directory does not exist.");
        }
    }

    private static boolean containsNonAlphabetic(String str) {
        // Use a regular expression to check for non-alphabetic characters
        return !str.matches("[a-zA-Z0-9'. ]+");
    }

    private static String getAccessPath(Access akses) {
        String dirPath = "";
        
        switch (akses) {
            case BIASA:
                dirPath = "C:" + File.separator + "Users" + File.separator +
                        "bpsbi" + File.separator + "OneDrive"
                        + File.separator + "Apartment Bintaro" + File.separator + "Data Harian" +
                        File.separator
                        + "BINTARO-APG";
                break;
            case WARUNG:
                dirPath = "C:" + File.separator + "Users" + File.separator +
                        "scg-b" + File.separator + "OneDrive"
                        + File.separator + "Data Harian" + File.separator
                        + "BINTARO-APG";
                break;
        }

        return dirPath;
    }

    public static void clearRevisiDir(Access akses) {
        String dirPath = getAccessPath(akses);
        File revisiDir = new File(dirPath + File.separator + "revisi");
        deleteContents(revisiDir);
    }

    private static void writeError(List<String> errorList, String filePath) {
        Path path = Paths.get(filePath);
        Path root = path.getParent().getParent();

        String writtenFilePathString = root.toString() + File.separator + "revisi" + File.separator
                + "Comprehensive_Error_Report_"
                + path.getFileName();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writtenFilePathString))) {
            writer.write("COMPREHENSIVE ERROR REPORT");
            writer.newLine();
            writer.write("Generated: " + new Date());
            writer.newLine();
            writer.write("Source File: " + filePath);
            writer.newLine();
            writer.write("Total Errors: " + errorList.size());
            writer.newLine();
            writer.write("-------------------------------------------");
            writer.newLine();

            // Categorize and write errors
            Map<String, List<String>> categorizedErrors = errorList.stream()
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

    public static void checkTempo(Access akses, Boolean csvFlag, Boolean summaryFlag) {
        String dirPath = getAccessPath(akses);
        List<InputObject> inputObjects = getInput(akses);
        String fileOutput = dirPath + File.separator + "OUTPUT";

        // Dependency Injection
        ErrorReporter errorReporter = new DefaultErrorReporter();
        FileProcessor fileProcessor = new DefaultFileProcessor(errorReporter);
        OutputWriter outputWriter = new DefaultOutputWriter();
        DateRangeProcessor dateRangeProcessor = new DateRangeProcessor();

        try (BufferedWriter fWriter = new BufferedWriter(new FileWriter(fileOutput))) {
            for (InputObject inputObj : inputObjects) {
                for (InputDate inputDate : inputObj.getInputDates()) {
                    String namaMandor = inputObj.getInputName();
                    String tanggalMulai = inputDate.getStartDate();
                    String tanggalAkhir = inputDate.getEndDate();

                    Path rekapPath = Paths.get(dirPath + File.separator + "rekap");
                    List<String> rekapFilenames = fileProcessor.readFiles(rekapPath.toString());

                    Map<String, Mandor> mapMandor = fileProcessor.processFiles(rekapFilenames, rekapPath.toString());

                    Mandor curMandor = mapMandor.get(namaMandor);

                    DateRangeProcessor.DateRange dateRange = dateRangeProcessor.parseDateRange(tanggalMulai,
                            tanggalAkhir);

                    int totalPayment = calculateTotalPayment(
                            curMandor,
                            dateRange.getStartDate(),
                            dateRange.getEndDate());

                    TreeMap<String, Integer> totalHutangTiapPekerja = calculateEachWorker(
                            curMandor,
                            dateRange.getStartDate(),
                            dateRange.getEndDate());

                    // Optional processing flags
                    if (csvFlag) {
                        for (String fileName : rekapFilenames) {
                            String filePath = rekapPath.toString() + File.separator + fileName;
                            writeCsv(mapMandor.get(fileName.split(" - ")[0]), filePath);
                        }
                    }

                    if (summaryFlag) {
                        for (String fileName : rekapFilenames) {
                            String filePath = rekapPath.toString() + File.separator + fileName;
                            writeSummary(mapMandor.get(fileName.split(" - ")[0]), filePath);
                        }
                    }

                    // Write output using the new OutputWriter
                    outputWriter.writeOutput(
                            fWriter,
                            curMandor,
                            tanggalMulai,
                            tanggalAkhir,
                            totalPayment,
                            totalHutangTiapPekerja);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
