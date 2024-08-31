package TempoObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InputObject {
    private String inputName;
    private List<InputDate> inputDateList = new ArrayList<>();

    public void setInputName(String input) {
        inputName = input;
    }

    public String getInputName() {
        return inputName;
    }

    public void setLinstInputDates(List<InputDate> input) {
        inputDateList = input;
    }

    public List<InputDate> getInputDates() {
        return inputDateList;
    }

    public void addInputDate(InputDate input) {
        inputDateList.add(input);
    }

}

class InputDate {
    private String startDate;
    private String endDate;

    public void setStartDate(String start) {
        startDate = formatDate(start);
    }

    public void setEndDate(String end) {
        endDate = formatDate(end);
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public static String formatDate(String inputDate) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy");

        try {
            // Parse the input date string into a Date object
            Date date = inputFormat.parse(inputDate);

            // Format the Date object into the desired output string
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null or handle the exception as needed
        }
    }
}
