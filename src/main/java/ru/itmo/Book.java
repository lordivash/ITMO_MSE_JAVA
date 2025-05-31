package ru.itmo;

import java.util.Date;

public class Book {
    private long id;
    private String title;
    private Date issueDate;
    private double rating;

    Book(long id, String title, Date issueDate, double rating) {
        this.id = id;
        this.title = title;
        this.issueDate = issueDate;
        this.rating = rating;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Book{ "
                + "id: " + id
                + ", title: " + title
                + ", issueDate: " + issueDate.toString()
                + ", rating: " + rating +
                " }";
    }
}