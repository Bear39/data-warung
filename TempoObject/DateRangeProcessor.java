package TempoObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRangeProcessor {
    private SimpleDateFormat dateFormat;

    public DateRangeProcessor() {
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy");
    }

    public DateRange parseDateRange(String tanggalMulai, String tanggalAkhir) {
        try {
            Date startDate = dateFormat.parse(tanggalMulai);
            Date endDate = dateFormat.parse(tanggalAkhir);
            return new DateRange(startDate, endDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }

    public static class DateRange {
        private Date startDate;
        private Date endDate;

        public DateRange(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }
    }
}
