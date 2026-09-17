public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    public Book(int id, String title, String author, String isbn, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = available;
    }

    public Book(String title, String author, String isbn) {
        this(-1, title, author, isbn, true);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("[%d] %-25s by %-20s (ISBN: %s) - %s",
                id, title, author, isbn, available ? "Available" : "Issued");
    }
}
