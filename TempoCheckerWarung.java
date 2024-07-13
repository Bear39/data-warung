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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TempoCheckerWarung {

    public static void main(String[] args) throws Exception {
        // String pathString = "C:" + File.separator + "Users" + File.separator +
        //         "bpsbi" + File.separator + "OneDrive"
        //         + File.separator + "Apartment Bintaro" + File.separator + "Data Harian" +
        //         File.separator
        //         + "BINTARO-APG";

        String pathString = "C:" + File.separator + "Users" + File.separator +
        "scg-b" + File.separator + "OneDrive"
        + File.separator + "Data Harian" + File.separator
        + "BINTARO-APG";

        clearRevisiDir(pathString);
        checkTempo(pathString);
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

    private static void checkTempo(String dirPath) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        List<String> rekapFilenames = new ArrayList<>();
        Path rekapPath = Paths.get(dirPath + File.separator + "rekap");

        if (Files.exists(rekapPath)) {
            if (Files.isDirectory(rekapPath)) {
                rekapFilenames = readFiles(rekapPath.toString());
            } else if (Files.isRegularFile(rekapPath)) {
                rekapFilenames.add(rekapPath.getFileName().toString());
            } else {
                System.out.println("NOT FOUND");
            }
        }

        Map<String, Mandor> mapMandor = new HashMap<>();

        for (String fileName : rekapFilenames) {
            List<String> errorMandor = new ArrayList<>();
            List<String> errorPekerja = new ArrayList<>();
            String mandorName = fileName.split(" - ")[0];
            Mandor mandor = null;
            if (mapMandor.get(mandorName) != null) {
                mandor = mapMandor.get(mandorName);
            } else {
                mandor = new Mandor();
                mandor.setNama(mandorName);
            }
            String filePath = rekapPath.toString() + File.separator + fileName;
            try (
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
                String line;
                int lineNumber = 0;
                int lastTempoLine = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    lineNumber++;
                    if (line.matches("\\d{1,2} \\w{3}.*")) {
                        if (!mandor.getListTempo().isEmpty()) {
                            Tempo lastTempo = mandor.getListTempo().get(mandor.getListTempo().size() - 1);
                            if (lastTempo.hasTotalTempo()) {
                                int realTotalTempo = lastTempo.getSetPekerja().stream()
                                        .mapToInt(Pekerja::getTotalUtang).sum();
                                if (realTotalTempo != lastTempo.getTotalTempo()) {
                                    String errorMessage = "Error penjumlahan total tempo, total jumlah "
                                            + realTotalTempo + " tercatat " + lastTempo.getTotalTempo();
                                    lastTempo.setHasError(true);
                                    lastTempo.setErrorMessage(errorMessage);
                                    errorMandor.add(errorMessage + " pada \""
                                            + lastTempo.getNamaTempo() + "\" line "
                                            + lastTempoLine);
                                }
                            } else {
                                int realTotalTempo = lastTempo.getSetPekerja().stream()
                                        .mapToInt(Pekerja::getTotalUtang).sum();
                                lastTempo.setTotalTempo(realTotalTempo);
                            }
                        }
                        String[] dataTempo = line.split(",");
                        Tempo tempo = new Tempo();
                        if (dataTempo.length == 2) {
                            if (dataTempo[0].split(" ")[0].length() == 1) {
                                dataTempo[0] = "0" + dataTempo[0];
                            }
                            tempo.setNamaTempo(dataTempo[0]);
                            try {
                                tempo.setWaktuTempo(dateFormat.parse(dataTempo[0]));
                            } catch (Exception e) {
                                e.printStackTrace();
                                String errorMessage = "Error input waktu tempo";
                                tempo.setHasError(true);
                                tempo.setErrorMessage(errorMessage);
                                errorMandor.add(errorMessage + " pada " + tempo.getNamaTempo()
                                        + "\" line " + lineNumber);
                            }
                            tempo.setTotalTempoExistence(true);
                            try {
                                tempo.setTotalTempo(Integer.parseInt(dataTempo[1]));
                            } catch (Exception e) {
                                e.printStackTrace();
                                String errorMessage = "Error total tempo bukan berupa angka";
                                tempo.setHasError(true);
                                tempo.setErrorMessage(errorMessage);
                                errorMandor.add(errorMessage + " pada " + tempo.getNamaTempo()
                                        + "\" line " + lineNumber);
                            }
                        } else if (dataTempo.length == 1) {
                            if (dataTempo[0].split(" ")[0].length() == 1) {
                                dataTempo[0] = "0" + dataTempo[0];
                            }
                            tempo.setNamaTempo(dataTempo[0]);
                            try {
                                tempo.setWaktuTempo(dateFormat.parse(dataTempo[0]));
                            } catch (Exception e) {
                                e.printStackTrace();
                                String errorMessage = "Error input waktu tempo";
                                tempo.setHasError(true);
                                tempo.setErrorMessage(errorMessage);
                                errorMandor.add(errorMessage + " pada " + tempo.getNamaTempo()
                                        + "\" line " + lineNumber);
                            }
                            tempo.setTotalTempoExistence(false);
                        } else {
                            String errorMessage = "Error input data tempo";
                            tempo.setHasError(true);
                            tempo.setErrorMessage(errorMessage);
                            errorMandor.add(errorMessage + " pada "
                                    + tempo.getNamaTempo() + " line " + lineNumber);
                        }
                        mandor.addTempo(tempo);
                        lastTempoLine = lineNumber;
                    } else {
                        Tempo tempo = null;
                        try {
                            tempo = mandor.getListTempo().get(mandor.getListTempo().size() - 1);
                        } catch (Exception e) {
                            System.out.println("ERROR!" + line);
                        }
                        String dataPekerja[] = line.split(",");
                        if (dataPekerja.length >= 3) {
                            Pekerja pekerja = new Pekerja();
                            if (containsNonAlphabetic(dataPekerja[0])) {
                                String errorMessage = "Error input nama pekerja";
                                pekerja.setHasError(true);
                                pekerja.setErrorMessage(errorMessage);
                                errorPekerja.add(errorMessage + " pada "
                                        + tempo.getNamaTempo() + " line " + lineNumber);
                            } else {
                                pekerja.setNama(dataPekerja[0]);
                            }
                            int totalDataPekerja = dataPekerja.length;
                            int totalUtangPekerja = 0;
                            try {
                                totalUtangPekerja = Integer.parseInt(dataPekerja[totalDataPekerja - 1]);
                            } catch (Exception e) {
                                e.printStackTrace();
                                String errorMessage = "Error dalam input total hutang";
                                pekerja.setHasError(true);
                                pekerja.setErrorMessage(errorMessage);
                                errorPekerja.add("Error dalam input total hutang " + pekerja.getNama() + " pada \""
                                        + tempo.getNamaTempo() + "\" line "
                                        + lineNumber);
                            }
                            pekerja.setTotalUtang(totalUtangPekerja);
                            for (int i = 1; i < totalDataPekerja - 1; i++) {
                                Transaksi transaksi = new Transaksi();
                                if (dataPekerja[i].contains("R")) {
                                    transaksi.setJenis(JenisTransaksi.ROKOK);
                                    try {
                                        transaksi.setHutang(
                                                Integer.parseInt(
                                                        dataPekerja[i].substring(0, dataPekerja[i].length() - 1)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        String errorMessage = "Error dalam input hutang";
                                        pekerja.setHasError(true);
                                        pekerja.setErrorMessage(errorMessage);
                                        errorPekerja.add("Error dalam input hutang " + pekerja.getNama() + " pada \""
                                                + tempo.getNamaTempo() + "\" line "
                                                + lineNumber);
                                    }
                                } else if (dataPekerja[i].contains("W")) {
                                    transaksi.setJenis(JenisTransaksi.WALLET);
                                    try {
                                        transaksi.setHutang(
                                                Integer.parseInt(
                                                        dataPekerja[i].substring(0, dataPekerja[i].length() - 1)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        String errorMessage = "Error dalam input hutang";
                                        pekerja.setHasError(true);
                                        pekerja.setErrorMessage(errorMessage);
                                        errorPekerja.add("Error dalam input hutang " + pekerja.getNama() + " pada \""
                                                + tempo.getNamaTempo() + "\" line "
                                                + lineNumber);
                                    }
                                } else {
                                    try {
                                        transaksi.setHutang(Integer.parseInt(dataPekerja[i]));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        String errorMessage = "Error dalam input hutang";
                                        pekerja.setHasError(true);
                                        pekerja.setErrorMessage(errorMessage);
                                        errorPekerja.add("Error dalam input hutang " + pekerja.getNama() + " pada \""
                                                + tempo.getNamaTempo() + "\" line "
                                                + lineNumber);
                                    }
                                }
                                pekerja.addTransaksi(transaksi);
                            }
                            int realTotalHutang = pekerja.getListTransaksi().stream().mapToInt(Transaksi::getHutang)
                                    .sum();
                            if (realTotalHutang != pekerja.getTotalUtang()) {
                                String errorMessage = "Error penjumlahan total transaksi " + pekerja.getNama()
                                        + ", total jumlah "
                                        + realTotalHutang + " tercatat " + pekerja.getTotalUtang();
                                pekerja.setHasError(true);
                                pekerja.setErrorMessage(errorMessage);
                                errorPekerja
                                        .add("Error penjumlahan total transaksi " + pekerja.getNama()
                                                + ", total jumlah "
                                                + realTotalHutang + " tercatat " + pekerja.getTotalUtang() + " pada \""
                                                + tempo.getNamaTempo() + "\" line " + lineNumber);
                            }
                            if (!tempo.addPekerja(pekerja)) {
                                int counter = 2;
                                String newName;
                                do {
                                    String errorMessage = "Error data " + pekerja.getNama();
                                    pekerja.setHasError(true);
                                    pekerja.setErrorMessage(errorMessage);
                                    errorPekerja
                                            .add("Error data " + pekerja.getNama() + " pada \"" + tempo.getNamaTempo()
                                                    + "\" line " + lineNumber + " sudah ada sebelumnya.");
                                    newName = pekerja.getNama() + " (" + counter++ + ")";
                                    pekerja.setNama(newName);
                                } while (!tempo.addPekerja(pekerja));
                            }
                        } else {
                            Pekerja pekerja = new Pekerja();
                            String errorMessage = "Error data " + pekerja.getNama();
                            pekerja.setHasError(true);
                            pekerja.setErrorMessage(errorMessage);
                            try {
                                errorPekerja
                                        .add("Error data pekerja tidak lengkap pada \"" + tempo.getNamaTempo()
                                                + "\" line "
                                                + lineNumber);
                            } catch (Exception e) {
                                System.out.println(line);
                            }
                        }
                    }
                }
                Tempo lastTempo = mandor.getListTempo().get(mandor.getListTempo().size() - 1);
                if (lastTempo.hasTotalTempo()) {
                    int realTotalTempo = lastTempo.getSetPekerja().stream()
                            .mapToInt(Pekerja::getTotalUtang).sum();
                    if (realTotalTempo != lastTempo.getTotalTempo()) {
                        String errorMessage = "Error penjumlahan total tempo, total jumlah "
                                + realTotalTempo + " tercatat " + lastTempo.getTotalTempo();
                        lastTempo.setHasError(true);
                        lastTempo.setErrorMessage(errorMessage);
                        errorMandor.add(errorMessage + " pada \""
                                + lastTempo.getNamaTempo() + "\" line "
                                + lastTempoLine);
                    }
                } else {
                    int realTotalTempo = lastTempo.getSetPekerja().stream()
                            .mapToInt(Pekerja::getTotalUtang).sum();
                    lastTempo.setTotalTempo(realTotalTempo);
                }

                Set<Tempo> uniqueTempoSet = new TreeSet<>(new TempoComparator());
                for (Tempo addedTempo : mandor.getListTempo()) {
                    if (!uniqueTempoSet.add(addedTempo)) {
                        int counter = 2;
                        String newName;
                        do {
                            errorPekerja.add("Error data \"" + addedTempo.getNamaTempo() + "\" sudah ada sebelumnya");
                            newName = addedTempo.getNamaTempo() + " (" + counter++ + ")";
                            addedTempo.setNamaTempo(newName);
                        } while (!uniqueTempoSet.add(addedTempo));
                    }
                }
                mandor.setListTempo(new ArrayList<>(uniqueTempoSet));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error pada pembacaan file " + fileName);
            }

            mapMandor.put(mandorName, mandor);

            if (!errorPekerja.isEmpty()) {
                writeError(errorPekerja, filePath);
            } else if (!errorMandor.isEmpty()) {
                writeError(errorMandor, filePath);
            }
            // writeCsv(mandor, filePath);
            writeSummary(mandor, filePath);
        }

        Mandor curMandor = mapMandor.get("Bambang");
        Date start = null;
        Date end = null;
        try {
            start = dateFormat.parse("08 June 2024");
            end = dateFormat.parse("22 June 2024");
        } catch (Exception e) {
            System.out.println("ERROR!!");
        }

        int totalPayment = calculateTotalPayment(curMandor, start, end);
        System.out.println(totalPayment);

        TreeMap<String, Integer> totalHutangTiapPekerja = calculateEachWorker(curMandor, start, end);
        System.out.println(
                "Total hutang per pekerja " + curMandor.getNama() + " tanggal " + start + "sampai " + end + " :");
        for (Map.Entry<String, Integer> entry : totalHutangTiapPekerja.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

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
            // Iterate through the List and write each element to the file with a new line
            for (Tempo dataTempo : mandor.getListTempo()) {
                writer.write(dataTempo.getWaktuTempo().toString() + "," + dataTempo.getTotalTempo().toString());
                writer.newLine();
            }
            System.out.println("Data has been written to the file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeValidatedCSV(Mandor mandor, String filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            for (Tempo tempo : mandor.getListTempo()) {
                bufferedWriter.write(tempo.getNamaTempo() + "," + tempo.getTotalTempo().toString());
                if (tempo.getHasError()) {
                    bufferedWriter.write("," + tempo.getErrorMessage());
                }
                bufferedWriter.newLine();
                for (Pekerja pekerja : tempo.getSetPekerja()) {
                    bufferedWriter.write(pekerja.getNama() + ",");
                    for (Transaksi transaksi : pekerja.getListTransaksi()) {
                        if (transaksi.getJenis() == JenisTransaksi.MAKAN) {
                            bufferedWriter.write(transaksi.getHutang().toString() + ",");
                        } else {
                            bufferedWriter.write(transaksi.getHutang().toString() + "R,");
                        }
                    }
                    bufferedWriter.write(pekerja.getTotalUtang().toString());
                    if (pekerja.getHasError()) {
                        bufferedWriter.write("," + pekerja.getErrorMessage());
                    }
                    bufferedWriter.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error pada pembacaan file");
        }
    }

    private static void clearRevisiDir(String dirPath) {
        File revisiDir = new File(dirPath + File.separator + "revisi");
        deleteContents(revisiDir);
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

    private static void writeError(List<String> errorList, String filePath) {
        Path path = Paths.get(filePath);
        Path root = path.getParent().getParent();

        String writtenFilePathString = root.toString() + File.separator + "revisi" + File.separator + "List Revisi "
                + path.getFileName();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writtenFilePathString))) {
            // Iterate through the List and write each element to the file with a new line
            for (String data : errorList) {
                writer.write(data);
                writer.newLine(); // Add a new line after each element
            }
            System.out.println("Data has been written to the file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteContents(File directory) {
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

}

class Mandor {
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

class TempoComparator implements Comparator<Tempo> {
    @Override
    public int compare(Tempo obj1, Tempo obj2) {
        return obj1.getNamaTempo().compareTo(obj2.getNamaTempo());
    }
}

class Tempo {
    private String namaTempo = "";
    private Date waktuTempo = null;
    private Set<Pekerja> setPekerja = new TreeSet<>(new PekerjaComparator());
    private Boolean totalTempoExist = false;
    private Integer totalTempo = 0;
    private Boolean hasError = false;
    private String errorMessage = "";

    public String getNamaTempo() {
        return namaTempo;
    }

    public void setNamaTempo(String namaTempo) {
        this.namaTempo = namaTempo;
    }

    public Set<Pekerja> getSetPekerja() {
        return setPekerja;
    }

    public Boolean addPekerja(Pekerja pekerja) {
        return this.setPekerja.add(pekerja);
    }

    public Boolean hasTotalTempo() {
        return totalTempoExist;
    }

    public void setTotalTempoExistence(Boolean totalTempoExistence) {
        this.totalTempoExist = totalTempoExistence;
    }

    public Integer getTotalTempo() {
        return totalTempo;
    }

    public void setTotalTempo(Integer totalTempo) {
        this.totalTempo = totalTempo;
    }

    @Override
    public String toString() {
        return "Tempo [namaTempo=" + namaTempo + ", setPekerja=" + setPekerja + ", hasTotalTempo=" + totalTempoExist
                + ", totalTempo=" + totalTempo + "]";
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getWaktuTempo() {
        return waktuTempo;
    }

    public void setWaktuTempo(Date waktuTempo) {
        this.waktuTempo = waktuTempo;
    }
}

class PekerjaComparator implements Comparator<Pekerja> {
    @Override
    public int compare(Pekerja obj1, Pekerja obj2) {
        return obj1.getNama().compareTo(obj2.getNama());
    }
}

class Pekerja {
    private String nama = "";
    private List<Transaksi> listTransaksi = new ArrayList<>();
    private Integer totalUtang = 0;
    private Boolean hasError = false;
    private String errorMessage = "";

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public List<Transaksi> getListTransaksi() {
        return listTransaksi;
    }

    public void addTransaksi(Transaksi transaksi) {
        this.listTransaksi.add(transaksi);
    }

    public Integer getTotalUtang() {
        return totalUtang;
    }

    public void setTotalUtang(Integer totalUtang) {
        this.totalUtang = totalUtang;
    }

    @Override
    public String toString() {
        return "Pekerja [nama=" + nama + ", listTransaksi=" + listTransaksi + ", totalUtang=" + totalUtang + "]";
    }

    public Boolean getHasError() {
        return hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}

enum JenisTransaksi {
    MAKAN, ROKOK, WALLET;
}

class Transaksi {
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