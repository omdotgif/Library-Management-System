import java.util.List;

public class ReportGenerator implements Reportable {

    private final Library library;

    public ReportGenerator(Library library) {
        this.library = library;
    }

    @Override
    public void printReport() {
        List<IssueRecord> records = library.getAllIssueRecords();

        System.out.println("\n--- Currently Issued Books ---");
        boolean anyIssued = false;
        for (IssueRecord r : records) {
            if (!r.isReturned()) {
                System.out.println(r);
                anyIssued = true;
            }
        }
        if (!anyIssued) System.out.println("(none)");

        System.out.println("\n--- Overdue Books ---");
        boolean anyOverdue = false;
        double totalPendingFine = 0.0;
        for (IssueRecord r : records) {
            if (r.isOverdue()) {
                System.out.println(r);
                anyOverdue = true;
            }
        }
        if (!anyOverdue) System.out.println("(none)");

        System.out.println("\n--- Fine Summary (collected) ---");
        double totalCollected = 0.0;
        for (IssueRecord r : records) {
            if (r.isReturned()) {
                totalCollected += r.getFine();
            }
        }
        System.out.printf("Total fines collected: %.2f%n", totalCollected);
    }
}
