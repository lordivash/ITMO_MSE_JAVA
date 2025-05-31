package ru.itmo;

import java.sql.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        BookDao bookAccess = new BookDao();
        bookAccess.prepareTable();
        Scanner scanner = new Scanner(System.in);

        Pattern addRegex = Pattern.compile("\"(.*?)\"\\s+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d+\\.\\d+)");
        Pattern updateRegex = Pattern.compile("(\\d+)\\s+\"(.*?)\"\\s+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d+\\.\\d+)");

        while (true) {
            System.out.print("command> ");
            String command = scanner.next();
            String rest = null;
            long id = 0;
            String title = null;
            Date date = null;
            Matcher matcher = null;
            double rating = 0;

            if (command.equals("exit")) break;

            switch (command) {
                case "add":
                    rest = scanner.nextLine();
                    matcher = addRegex.matcher(rest);
                    if (matcher.find()) {
                        title = matcher.group(1);
                        date = parseDate(matcher.group(2));
                        rating = Double.parseDouble(matcher.group(3));
                    } else {
                        System.out.println("Incorrect input format");
                    }
                    bookAccess.addBook(title, date, rating);
                    break;
                case "delete":
                    id = scanner.nextLong();
                    bookAccess.deleteBook(id);
                    break;
                case "update":
                    rest = scanner.nextLine();
                    matcher = updateRegex.matcher(rest);

                    if (matcher.find()) {
                        id = Long.parseLong(matcher.group(1));
                        title = matcher.group(2);
                        date = parseDate(matcher.group(3));
                        rating = Double.parseDouble(matcher.group(4));
                    } else {
                        System.out.println("Incorrect input format");
                    }
                    bookAccess.updateBook(id, title, date, rating);
                    break;
                case "print":
                    id = scanner.nextLong();
                    bookAccess.printBook(id);
                    break;
                case "printAll":
                    bookAccess.printAllBooks();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        }

        scanner.close();
    }

    private static Date parseDate(String date) {
        String[] dateAtoms = date.split("-");
        int year = Integer.parseInt(dateAtoms[0]) - 1900;
        int month = Integer.parseInt(dateAtoms[1]) - 1;
        int day = Integer.parseInt(dateAtoms[2]);

        return new Date(year, month, day);
    }
}
