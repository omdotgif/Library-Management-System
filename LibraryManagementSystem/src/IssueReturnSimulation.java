public class IssueReturnSimulation {

    private final Library library;
    private final int bookId;

    public IssueReturnSimulation(Library library, int bookId) {
        this.library = library;
        this.bookId = bookId;
    }

    public void run(int memberIdA, int memberIdB) {
        Thread t1 = new Thread(() -> attemptIssue(memberIdA), "Member-A-Thread");
        Thread t2 = new Thread(() -> attemptIssue(memberIdB), "Member-B-Thread");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulation interrupted: " + e.getMessage());
        }
    }

    private void attemptIssue(int memberId) {
        try {
            library.issueBook(bookId, memberId);
            System.out.println(Thread.currentThread().getName() +
                    ": successfully issued book " + bookId + " to member " + memberId);
        } catch (BookNotAvailableException | MemberNotFoundException e) {
            System.out.println(Thread.currentThread().getName() +
                    ": failed to issue book " + bookId + " -> " + e.getMessage());
        }
    }
}
