import java.time.LocalDate;

public class IssueRecord {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null while the book is still out
    private double fine;

    public IssueRecord(int id, int bookId, int memberId, LocalDate issueDate,
                        LocalDate dueDate, LocalDate returnDate, double fine) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public boolean isReturned() { return returnDate != null; }

    public boolean isOverdue() {
        return !isReturned() && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return String.format("Record[%d] Book:%d Member:%d Issued:%s Due:%s Returned:%s Fine:%.2f",
                id, bookId, memberId, issueDate, dueDate,
                returnDate == null ? "-" : returnDate.toString(), fine);
    }
}
