import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class FineCalculator {

    private static final double FINE_PER_DAY = 5.0; // currency units per day late

    private FineCalculator() {
        // utility class - prevent instantiation
    }

    public static double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate == null || !returnDate.isAfter(dueDate)) {
            return 0.0;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate * FINE_PER_DAY;
    }
}
