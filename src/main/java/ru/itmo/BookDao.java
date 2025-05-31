package ru.itmo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.postgresql.ds.PGPoolingDataSource;

public class BookDao {
    private static final String DATABASE = "postgres";
    private static final String SERVER = "localhost";
    private static final int PORT = 5432;
    private static final String USER = "test";
    private static final String PASSWORD = "test";

    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS books;";
    private static final String INSERT_BOOK_SQL = "INSERT INTO books (title, issue_date, rating) VALUES (?, ?, ?);";
    private static final String DELETE_BOOK_SQL = "DELETE FROM books WHERE id = ?;";
    private static final String UPDATE_BOOK_SQL = "UPDATE books SET title = ?, issue_date = ?, rating = ? WHERE id = ?";
    private static final String SELECT_BOOK_SQL = "SELECT * FROM books WHERE id = ?;";
    private static final String SELECT_ALL_SQL = "SELECT * FROM books;";
    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE books (
            id BIGSERIAL PRIMARY KEY,
            title TEXT,
            issue_date DATE,
            rating DOUBLE PRECISION
        );
        """;

    final PGPoolingDataSource source;

    BookDao() {
        source = new PGPoolingDataSource();
        source.setDatabaseName(DATABASE);
        source.setServerName(SERVER);
        source.setUser(USER);
        source.setPassword(PASSWORD);
        source.setPortNumber(PORT);
    }

    public void prepareTable() {
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.addBatch(DROP_TABLE_SQL);
            stmt.addBatch(CREATE_TABLE_SQL);
            stmt.executeBatch();
        } catch (SQLException e) {
            // Log here
            throw new RuntimeException("Failed to prepare work table. Terminating.");
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();

        try (Connection connection = getConnection()) {
            ResultSet rs =  connection.createStatement().executeQuery(SELECT_ALL_SQL);
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDate("issue_date"),
                        rs.getDouble("rating")
                ));
            }
        }

        return books;
    }

    public void printAllBooks() {
        List<Book> books = null;
        try {
            books = getAllBooks();
        } catch (SQLException e) {
            // Log here
            System.out.println("Couldn't perform operation: Database error has occurred");
            return;
        }

        for (Book book : books) {
            System.out.println(book);
        }
    }

    public Book getBook(long id) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(SELECT_BOOK_SQL);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            return new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDate("issue_date"),
                    rs.getDouble("rating")
            );
        }
    }

    public void printBook(long id) {
        Book book = null;
        try {
            book = getBook(id);
        } catch (SQLException e) {
            // Log here
            System.out.println("Couldn't perform operation: Database error has occurred");
            return;
        }

        System.out.println((book == null ? "book with id " + id + " doesn't exist" : book.toString()));
    }

    public void addBook(String title, Date issueDate, double rating) {
        try (Connection connection = getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(INSERT_BOOK_SQL);
            stmt.setString(1, title);
            stmt.setDate(2, issueDate);
            stmt.setDouble(3, rating);
            int status = stmt.executeUpdate();

            System.out.println(status + " row" + (status == 0 ? "s" : "") + " affected");
        } catch (SQLException e) {
            // Log here
            System.out.println("Couldn't perform operation: Database error has occurred");
        }
    }

    public void deleteBook(long id) {
        try (Connection connection = getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(DELETE_BOOK_SQL);
            stmt.setLong(1, id);
            int status = stmt.executeUpdate();

            System.out.println(status + " row" + (status == 0 ? "s" : "") + " affected");
        } catch (SQLException e) {
            // Log here
            System.out.println("Couldn't perform operation: Database error has occurred");
        }
    }

    public void updateBook(long id, String title, Date issueDate, double rating) {
        try (Connection connection = getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(UPDATE_BOOK_SQL);
            stmt.setString(1, title);
            stmt.setDate(2, issueDate);
            stmt.setDouble(3, rating);
            stmt.setLong(4, id);
            int status = stmt.executeUpdate();

            System.out.println(status + " row" + (status == 0 ? "s" : "") + " affected");
        } catch (SQLException e) {
            // Log here
            System.out.println("Couldn't perform operation: Database error has occurred");
        }
    }

    private Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASSWORD);
        return source.getConnection();
    }
}
