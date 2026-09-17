import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Library library = new Library();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DBConnection.initSchema();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addBook();
                    case "2" -> searchBooks();
                    case "3" -> addMember();
                    case "4" -> issueBook();
                    case "5" -> returnBook();
                    case "6" -> new ReportGenerator(library).printReport();
                    case "7" -> runConcurrencyDemo();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n===== Library Management System =====");
        System.out.println("1. Add Book");
        System.out.println("2. Search Books");
        System.out.println("3. Register Member");
        System.out.println("4. Issue Book");
        System.out.println("5. Return Book");
        System.out.println("6. View Reports");
        System.out.println("7. Simulate Concurrent Issue Requests (demo)");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            System.out.println("All fields are required. Book not added.");
            return;
        }
        library.addBook(new Book(title, author, isbn));
        System.out.println("Book added.");
    }

    private static void searchBooks() {
        System.out.print("Search keyword (title/author/ISBN): ");
        String keyword = scanner.nextLine().trim();
        List<Book> results = library.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void addMember() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty()) {
            System.out.println("All fields are required. Member not added.");
            return;
        }
        library.addMember(new Member(name, email));
        System.out.println("Member registered.");
    }

    private static void issueBook() {
        int bookId = readInt("Book ID: ");
        int memberId = readInt("Member ID: ");
        try {
            library.issueBook(bookId, memberId);
        } catch (BookNotAvailableException | MemberNotFoundException e) {
            System.out.println("Could not issue book: " + e.getMessage());
        }
    }

    private static void returnBook() {
        int recordId = readInt("Issue Record ID: ");
        double fine = library.returnBook(recordId);
        if (fine > 0) {
            System.out.printf("Fine to collect: %.2f%n", fine);
        }
    }

    private static void runConcurrencyDemo() {
        int bookId = readInt("Book ID to contest: ");
        int memberA = readInt("Member A ID: ");
        int memberB = readInt("Member B ID: ");
        new IssueReturnSimulation(library, bookId).run(memberA, memberB);
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
