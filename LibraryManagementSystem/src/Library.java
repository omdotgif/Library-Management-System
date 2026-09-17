import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Library {

    private static final int LOAN_PERIOD_DAYS = 14;

//Book Management

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, available) VALUES (?, ?, ?, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
        }
    }

    public void deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        String pattern = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return results;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books: " + e.getMessage());
        }
        return books;
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("available") == 1
        );
    }

//Member Management

    public void addMember(Member member) {
        String sql = "INSERT INTO members (name, email) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
        }
    }

    public Member getMemberById(int id) throws MemberNotFoundException {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Member(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching member: " + e.getMessage());
        }
        throw new MemberNotFoundException("No member found with ID " + id);
    }

//Issue / Return

    public synchronized void issueBook(int bookId, int memberId)
            throws BookNotAvailableException, MemberNotFoundException {

        getMemberById(memberId);

        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT available FROM books WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new BookNotAvailableException("No book found with ID " + bookId);
                    }
                    if (rs.getInt("available") == 0) {
                        throw new BookNotAvailableException(
                                "Book ID " + bookId + " is already issued to another member.");
                    }
                }
            }

            String updateSql = "UPDATE books SET available = 0 WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
            String insertSql = "INSERT INTO issue_records (book_id, member_id, issue_date, due_date, fine) " +
                    "VALUES (?, ?, ?, ?, 0)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, bookId);
                ps.setInt(2, memberId);
                ps.setString(3, issueDate.toString());
                ps.setString(4, dueDate.toString());
                ps.executeUpdate();
            }

            System.out.println("Book " + bookId + " issued to member " + memberId +
                    ". Due date: " + dueDate);

        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
        }
    }

    public synchronized double returnBook(int issueRecordId) {
        String selectSql = "SELECT * FROM issue_records WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {

            int bookId;
            LocalDate dueDate;
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, issueRecordId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No issue record found with ID " + issueRecordId);
                        return 0.0;
                    }
                    bookId = rs.getInt("book_id");
                    dueDate = LocalDate.parse(rs.getString("due_date"));
                }
            }

            LocalDate returnDate = LocalDate.now();
            double fine = FineCalculator.calculateFine(dueDate, returnDate);

            String updateRecordSql = "UPDATE issue_records SET return_date = ?, fine = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateRecordSql)) {
                ps.setString(1, returnDate.toString());
                ps.setDouble(2, fine);
                ps.setInt(3, issueRecordId);
                ps.executeUpdate();
            }

            String updateBookSql = "UPDATE books SET available = 1 WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBookSql)) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            System.out.println("Book " + bookId + " returned. Fine due: " + fine);
            return fine;

        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return 0.0;
        }
    }

    public List<IssueRecord> getAllIssueRecords() {
        List<IssueRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM issue_records";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String returnDateStr = rs.getString("return_date");
                records.add(new IssueRecord(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        LocalDate.parse(rs.getString("issue_date")),
                        LocalDate.parse(rs.getString("due_date")),
                        returnDateStr == null ? null : LocalDate.parse(returnDateStr),
                        rs.getDouble("fine")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching issue records: " + e.getMessage());
        }
        return records;
    }
}
