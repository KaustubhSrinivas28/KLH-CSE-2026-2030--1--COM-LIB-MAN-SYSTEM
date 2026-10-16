// ============== COMMUNITY LIBRARY MANAGEMENT SYSTEM ==============
// M1 basics · M2 control flow · M3 methods & arrays · M4 OOP & exceptions
// M5 strings, sorting & files · M6 collections, lambdas & streams
// Requires Java 8 or above (Module 6 uses lambdas and streams).

import java.io.File;                  // Module 5: points to a file
import java.io.FileNotFoundException; // Module 4: checked exception
import java.io.FileWriter;            // Module 5: writes into a file
import java.io.IOException;           // Module 4: checked exception
import java.util.ArrayList;           // Module 6: resizable typed collection
import java.util.Arrays;              // Module 5: sort and binary search
import java.util.Collections;         // Module 6: Collections.sort()
import java.util.Comparator;          // Module 5: a second way of sorting
import java.util.List;                // Module 6: the List interface
import java.util.Map;                 // Module 6: key-value collection
import java.util.Optional;            // Module 6: a value that may be absent
import java.util.Scanner;             // Module 1: input (also reads the file)
import java.util.TreeMap;             // Module 6: a Map kept in key order
import java.util.function.Predicate;  // Module 6: a functional interface
import java.util.stream.Collectors;   // Module 6: collecting stream results

// ===== MODULE 4: INTERFACE — every borrowable item must do these three things =====
interface Borrowable {
    boolean isIssued();
    void issue();
    void giveBack();
}

// ===== MODULE 4: OUR OWN CHECKED EXCEPTION (inherits from Exception) =====
class BookNotFoundException extends Exception {
    BookNotFoundException(String message) {
        super(message);   // Module 4: super() passes the text to the parent class
    }
}

// ===== MODULE 4: CLASS = BLUEPRINT, OBJECT = ONE BOOK =====
class Book implements Borrowable, Comparable<Book> {

    // Module 4: private fields — encapsulation / information hiding
    private int id;
    private String title, author, category;
    private boolean issued;

    // Module 4: static member — one copy shared by every object
    private static int totalBooksCreated = 0;

    // Module 4: parameterized constructor ('this' separates field from parameter)
    Book(int id, String title, String author, String category) {
        this.id = id;
        setTitle(title);            // the setters also do the checking
        setAuthor(author);
        this.category = category;
        this.issued = false;        // a new book is always available
        totalBooksCreated++;
    }

    // Module 4: constructor overloading — used when reading a saved file,
    // because there we already know whether the book was issued
    Book(int id, String title, String author, String category, boolean issued) {
        this(id, title, author, category);
        this.issued = issued;
    }

    // Module 4: accessors (getters)
    int getId() { return id; }
    String getTitle() { return title; }
    String getAuthor() { return author; }
    String getCategory() { return category; }

    // Module 4: mutators (setters) with validation — Module 5: trim(), isEmpty()
    void setTitle(String t)  { title  = (t == null || t.trim().isEmpty()) ? "Unknown Title"  : t.trim(); }
    void setAuthor(String a) { author = (a == null || a.trim().isEmpty()) ? "Unknown Author" : a.trim(); }

    // Module 4: the three methods promised to the Borrowable interface
    public boolean isIssued() { return issued; }
    public void issue() { issued = true; }
    public void giveBack() { issued = false; }

    String getStatus() { return issued ? "Issued" : "Available"; }  // Module 1: ternary

    // Module 4: toString() is inherited from Object and overridden here, so that
    // printing a Book prints a neat table row.  Module 5: substring() cuts a long title
    public String toString() {
        String shortTitle = (title.length() > 25) ? title.substring(0, 22) + "..." : title;
        return String.format("%-6d %-26s %-20s %-12s %-10s",
                id, shortTitle, author, category, getStatus());
    }

    // Module 5: builds one comma-separated line for the data file
    String toFileLine() {
        return id + "," + title + "," + author + "," + category + "," + issued;
    }

    // Module 5: Comparable — the natural order of books is by ID
    public int compareTo(Book other) { return this.id - other.id; }

    static int getTotalBooksCreated() { return totalBooksCreated; }
}

// ===== MODULE 5: Comparator — a second ordering, by title =====
class TitleComparator implements Comparator<Book> {
    public int compare(Book b1, Book b2) {
        return b1.getTitle().compareToIgnoreCase(b2.getTitle());
    }
}

public class CommunityLibrary {

    // ===== MODULE 1: CONSTANTS =====
    static final int MAX_BOOKS = 50;
    static final int AVAILABLE = 0, ISSUED = 1;      // columns of the 2-D table
    static final String DATA_FILE = "library.txt";
    static final String LOG_FILE = "library_log.txt";

    // ===== MODULE 3: 1-D ARRAY of Book objects =====
    static Book[] books = new Book[MAX_BOOKS];
    static int count = 0;
    static String[] categoryNames = {"Fiction", "Non-Fiction", "Children", "Reference"};

    static Scanner sc = new Scanner(System.in);      // Module 1: keyboard input

    public static void main(String[] args) {
        boolean running = true;
        System.out.println("Welcome to the Community Library!");

        // ===== MODULE 2: WHILE LOOP + SWITCH =====
        while (running) {
            displayMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:  addBooks();       break;
                case 2:  displayBooks();   break;
                case 3:  searchBook();     break;
                case 4:  issueBook();      break;
                case 5:  returnBook();     break;
                case 6:  showStatistics(); break;
                case 7:  saveBooks();      break;
                case 8:  loadBooks();      break;
                case 9:  smartReports();   break;
                case 10:
                    System.out.println("Thank you for using the Community Library System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a number from 1 to 10.");
            }
        }
        sc.close();
    }

    // ===== MODULE 3: METHOD WITHOUT PARAMETERS =====
    static void displayMenu() {
        System.out.println("\n===== COMMUNITY LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Book            6. Library Statistics");
        System.out.println("2. Display Books       7. Save Books to File");
        System.out.println("3. Search Book         8. Load Books from File");
        System.out.println("4. Issue Book          9. Smart Reports (Collections & Streams)");
        System.out.println("5. Return Book        10. Exit");
    }

    // Module 3: method with a parameter and a return value
    // Module 4: try-catch so that wrong typing does not stop the program
    static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("That is not a whole number. Please try again.");
            }
        }
    }

    static String readText(String message) {         // Module 5: trim() cleans the input
        System.out.print(message);
        return sc.nextLine().trim();
    }

    // ===== MODULE 3: LINEAR SEARCH + MODULE 4: throw / throws =====
    static Book findBook(int id) throws BookNotFoundException {
        for (int i = 0; i < count; i++) {
            if (books[i].getId() == id) return books[i];
        }
        throw new BookNotFoundException("No book found with ID " + id + ".");
    }

    // Module 3: method overloading — search by part of the title
    // Module 5: toLowerCase() + contains() allow a partial match
    static Book findBook(String keyword) throws BookNotFoundException {
        String key = keyword.toLowerCase();
        for (int i = 0; i < count; i++) {
            if (books[i].getTitle().toLowerCase().contains(key)) return books[i];
        }
        throw new BookNotFoundException("No book with title containing \"" + keyword + "\".");
    }

    static boolean idExists(int id) {                // simple check before adding
        for (int i = 0; i < count; i++) if (books[i].getId() == id) return true;
        return false;
    }

    // ===== FEATURE 1: ADD BOOKS =====
    static void addBooks() {
        int howMany = readInt("How many books do you want to add? ");
        if (howMany <= 0) {
            System.out.println("Please enter a number greater than 0.");
            return;
        }
        int added = 0;                               // Module 3: counting technique
        for (int i = 1; i <= howMany; i++) {         // Module 2: for loop
            if (count == MAX_BOOKS) {
                System.out.println("The library is full. No more books can be added.");
                break;                               // Module 2: leave the loop early
            }
            System.out.println("\n--- Book " + i + " of " + howMany + " ---");
            if (addBook()) added++;
        }
        System.out.println("\n" + added + " book(s) added. Total books: " + count);
    }

    // Adds ONE book. Returns true if it was added.
    static boolean addBook() {
        int id = readInt("Enter Book ID: ");
        if (id <= 0) {
            System.out.println("Book ID must be a positive number. Not added.");
            return false;
        }
        if (idExists(id)) {
            System.out.println("A book with ID " + id + " already exists. Not added.");
            return false;
        }

        String title = readText("Enter Book Name: ");
        String author = readText("Enter Author Name: ");
        if (title.isEmpty() || author.isEmpty()) {   // Module 5: isEmpty() validation
            System.out.println("Name and author cannot be empty. Not added.");
            return false;
        }
        System.out.print("Categories: ");            // Module 3: array traversal
        for (int i = 0; i < categoryNames.length; i++) {
            System.out.print((i + 1) + ". " + categoryNames[i] + "   ");
        }
        System.out.println();
        int category;                                // Module 2: do-while loop
        do {
            category = readInt("Enter Category (1-" + categoryNames.length + "): ");
            if (category < 1 || category > categoryNames.length) {
                System.out.println("Invalid category, try again.");
            }
        } while (category < 1 || category > categoryNames.length);

        // ===== MODULE 4: CREATING AN OBJECT WITH new =====
        // 'b' is a reference variable; it holds null until an object is given to it
        Book b = new Book(id, title, author, categoryNames[category - 1]);
        books[count] = b;
        count++;
        System.out.println("Book added successfully!");
        return true;
    }

    // ===== FEATURE 2: DISPLAY ALL BOOKS =====
    static void displayBooks() {
        if (count == 0) {
            System.out.println("No books in the library yet.");
            return;
        }
        System.out.println("Sort by: 1. Book ID   2. Title   3. Do not sort");
        int option = readInt("Enter option: ");

        // ===== MODULE 5: SORTING =====
        if (option == 1) {
            Arrays.sort(books, 0, count);                        // uses compareTo()
        } else if (option == 2) {
            Arrays.sort(books, 0, count, new TitleComparator()); // uses the Comparator
        }
        System.out.printf("%n%-6s %-26s %-20s %-12s %-10s%n",
                "ID", "Book Name", "Author", "Category", "Status");
        System.out.println("------------------------------------------------------------------------------");
        for (int i = 0; i < count; i++) {            // Module 3: array traversal
            System.out.println(books[i]);            // Module 4: toString() is used here
        }
    }

    // ===== FEATURE 3: SEARCH FOR A BOOK =====
    static void searchBook() {
        if (count == 0) {
            System.out.println("No books in the library yet.");
            return;
        }
        System.out.println("Search by: 1. ID (linear)   2. Title keyword   3. ID (binary)");
        int option = readInt("Enter option: ");
        try {                                        // Module 4: catching our exception
            if (option == 1) {
                System.out.println("Book found!\n" + findBook(readInt("Enter Book ID: ")));
            } else if (option == 2) {
                System.out.println("Book found!\n" + findBook(readText("Enter part of the title: ")));
            } else if (option == 3) {
                fastSearch(readInt("Enter Book ID: "));
            } else {
                System.out.println("Invalid option.");
            }
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // ===== MODULE 5: Arrays.sort() + Arrays.binarySearch() =====
    // Binary search needs sorted data, so the IDs are copied and sorted first.
    static void fastSearch(int id) throws BookNotFoundException {
        int[] ids = new int[count];
        for (int i = 0; i < count; i++) ids[i] = books[i].getId();
        Arrays.sort(ids);
        int pos = Arrays.binarySearch(ids, id);
        if (pos < 0) {
            throw new BookNotFoundException("Binary search: ID " + id + " is not in the library.");
        }
        System.out.println("Binary search found ID " + id + " at position " + (pos + 1)
                + " of the sorted ID list.");
        System.out.println(findBook(id));
    }

    // ===== FEATURE 4: ISSUE A BOOK =====
    static void issueBook() {
        int id = readInt("Enter Book ID to issue: ");
        try {                                        // Module 4: try - catch - finally
            Book b = findBook(id);
            if (b.isIssued()) {
                System.out.println("Sorry, \"" + b.getTitle() + "\" is already issued.");
            } else {
                b.issue();
                System.out.println("\"" + b.getTitle() + "\" has been issued successfully.");
                logAction("ISSUE," + b.getId() + "," + b.getTitle());
            }
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("(Issue operation finished.)");   // finally always runs
        }
    }

    // ===== FEATURE 5: RETURN A BOOK =====
    static void returnBook() {
        int id = readInt("Enter Book ID to return: ");
        try {
            Book b = findBook(id);
            if (!b.isIssued()) {
                System.out.println("\"" + b.getTitle() + "\" is already available.");
            } else {
                b.giveBack();
                System.out.println("\"" + b.getTitle() + "\" has been returned. Thank you!");
                logAction("RETURN," + b.getId() + "," + b.getTitle());
            }
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // ===== MODULE 3: COUNTING TECHNIQUE =====
    static int countIssued() {
        int total = 0;
        for (int i = 0; i < count; i++) if (books[i].isIssued()) total++;
        return total;
    }

    static int categoryIndex(String category) {      // Module 3: linear search
        for (int i = 0; i < categoryNames.length; i++)
            if (categoryNames[i].equalsIgnoreCase(category)) return i;
        return -1;
    }

    // ===== FEATURE 6: LIBRARY STATISTICS =====
    static void showStatistics() {
        if (count == 0) {
            System.out.println("No books in the library yet.");
            return;
        }
        int issuedCount = countIssued();
        int availableCount = count - issuedCount;

        // Module 1: typecasting so the division keeps its decimal part
        double issuedPercent = (double) issuedCount / count * 100;
        double averageIssued = (double) issuedCount / categoryNames.length;
        System.out.println("\n===== LIBRARY STATISTICS =====");
        System.out.println("Total books     : " + count);
        System.out.println("Available books : " + availableCount);
        System.out.println("Issued books    : " + issuedCount);
        System.out.printf("Books issued    : %.1f%%%n", issuedPercent);
        System.out.println("Book objects created : " + Book.getTotalBooksCreated());

        // ===== MODULE 3: 2-D ARRAY — rows = categories, columns = available / issued =====
        int[][] categoryTable = new int[categoryNames.length][2];
        for (int i = 0; i < count; i++) {
            int row = categoryIndex(books[i].getCategory());
            if (row == -1) continue;                 // Module 2: skip unknown category
            categoryTable[row][books[i].isIssued() ? ISSUED : AVAILABLE]++;
        }
        System.out.println();
        System.out.print(buildCategoryReport(categoryTable));
        System.out.printf("%nAverage issued books per category: %.2f%n", averageIssued);
        if (issuedCount > 0) {                       // Module 3: finding the maximum
            int best = 0;
            for (int r = 1; r < categoryNames.length; r++)
                if (categoryTable[r][ISSUED] > categoryTable[best][ISSUED]) best = r;
            System.out.println("Most borrowed category right now: " + categoryNames[best]);
        }
    }

    // ===== MODULE 5: StringBuilder builds the report text =====
    static String buildCategoryReport(int[][] table) {
        StringBuilder sb = new StringBuilder("Category-wise report:\n");
        sb.append(String.format("%-12s %-10s %-8s %-6s%n",
                "Category", "Available", "Issued", "Total"));
        for (int r = 0; r < categoryNames.length; r++) {   // Module 2: nested loops
            int rowTotal = 0;
            for (int value : table[r]) rowTotal += value;  // Module 3: for-each summation
            sb.append(String.format("%-12s %-10d %-8d %-6d%n", categoryNames[r],
                    table[r][AVAILABLE], table[r][ISSUED], rowTotal));
        }
        return sb.toString();
    }

    // ===== FEATURE 7: SAVE BOOKS INTO A FILE (Module 5) =====
    static void saveBooks() {
        if (count == 0) {
            System.out.println("Nothing to save.");
            return;
        }
        // Module 5: try-with-resources closes the file automatically
        try (FileWriter out = new FileWriter(DATA_FILE)) {
            for (int i = 0; i < count; i++) out.write(books[i].toFileLine() + "\n");
            System.out.println(count + " record(s) saved to " + DATA_FILE);
        } catch (IOException e) {                    // Module 4: checked exception
            System.out.println("Could not save the file: " + e.getMessage());
        }
    }

    // ===== FEATURE 8: LOAD BOOKS FROM THE FILE (Module 5) =====
    static void loadBooks() {
        File f = new File(DATA_FILE);
        if (!f.exists()) {                           // Module 5: missing file handled
            System.out.println("No data file named " + DATA_FILE + " was found.");
            return;
        }
        int loaded = 0, skipped = 0;
        count = 0;                                   // start again with the file data

        // Module 5: a Scanner can read a file too; try-with-resources closes it
        try (Scanner fileIn = new Scanner(f)) {
            while (fileIn.hasNextLine()) {           // Module 2: line-by-line reading
                String line = fileIn.nextLine().trim();
                if (line.isEmpty()) continue;        // Module 2: skip blank lines
                if (count == MAX_BOOKS) {
                    System.out.println("Library is full, remaining lines were not read.");
                    break;
                }
                String[] parts = line.split(",");    // Module 5: tokenizing a record
                if (parts.length < 5) {
                    System.out.println("Malformed record skipped: " + line);
                    skipped++;
                    continue;
                }
                try {
                    books[count] = new Book(Integer.parseInt(parts[0].trim()),
                            parts[1].trim(), parts[2].trim(), parts[3].trim(),
                            Boolean.parseBoolean(parts[4].trim()));
                    count++;
                    loaded++;
                // Module 4: multi-catch handles two kinds of damaged record
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Bad or incomplete record skipped: " + line);
                    skipped++;
                }
            }
            System.out.println(loaded + " record(s) loaded, " + skipped + " skipped.");
        } catch (FileNotFoundException e) {
            System.out.println("Could not open the file: " + e.getMessage());
        }
    }

    // ===== FEATURE 9: SMART REPORTS (MODULE 6) =====
    // Collections, generics, lambdas, Predicate, streams, Collectors and Optional.
    static void smartReports() {
        if (count == 0) {
            System.out.println("No books in the library yet.");
            return;
        }

        // Module 6: copy the plain array into a typed collection (generics)
        List<Book> list = new ArrayList<Book>();
        for (int i = 0; i < count; i++) list.add(books[i]);
        System.out.println("\n===== SMART REPORTS (Collections & Streams) =====");
        System.out.println("Books copied into an ArrayList : " + list.size());

        // Module 6: Collections.sort() with a Comparator written as a lambda
        Collections.sort(list, (b1, b2) -> b1.getAuthor().compareToIgnoreCase(b2.getAuthor()));
        System.out.println("\n-- Sorted by author (Collections.sort + lambda) --");
        list.forEach(b -> System.out.println("   " + b.getAuthor() + " - " + b.getTitle()));

        // Module 6: a Predicate is a functional interface, written here as a lambda
        Predicate<Book> isAvailable = b -> !b.isIssued();

        // Module 6: stream pipeline — filter, map, sorted, collect
        List<String> availableTitles = list.stream()
                .filter(isAvailable)
                .map(b -> b.getTitle())
                .sorted()
                .collect(Collectors.toList());
        System.out.println("\n-- Available titles (filter / map / sorted / collect) --");
        System.out.println("   " + availableTitles);

        // Module 6: Collectors.joining() builds one String from the stream
        String issuedTitles = list.stream().filter(b -> b.isIssued())
                .map(b -> b.getTitle()).collect(Collectors.joining(", "));
        System.out.println("-- Issued titles (Collectors.joining) --");
        System.out.println("   " + (issuedTitles.isEmpty() ? "none" : issuedTitles));

        // Module 6: groupingBy + counting give a Map; TreeMap keeps it in key order
        Map<String, Long> byCategory = new TreeMap<String, Long>(list.stream()
                .collect(Collectors.groupingBy(b -> b.getCategory(), Collectors.counting())));
        System.out.println("\n-- Books per category (groupingBy + counting) --");
        for (Map.Entry<String, Long> entry : byCategory.entrySet()) {   // for-each on a Map
            System.out.println("   " + entry.getKey() + " : " + entry.getValue());
        }

        // Module 6: count() terminal operation
        long freeNow = list.stream().filter(isAvailable).count();
        System.out.println("\nAvailable right now (stream count) : " + freeNow);

        // Module 6: distinct() and limit() in a pipeline
        List<String> someAuthors = list.stream().map(b -> b.getAuthor())
                .distinct().sorted().limit(3).collect(Collectors.toList());
        System.out.println("First three distinct authors       : " + someAuthors);

        // Module 6: Optional holds a value that may be absent, so no null check is needed
        Optional<Book> firstIssued = list.stream().filter(b -> b.isIssued()).findFirst();
        System.out.println("First issued book (Optional)       : "
                + (firstIssued.isPresent() ? firstIssued.get().getTitle() : "NONE"));
    }

    // ===== MODULE 5: APPENDING A LINE TO THE LOG FILE =====
    // 'true' means add at the end — do not erase the old lines.
    static void logAction(String action) {
        try (FileWriter out = new FileWriter(LOG_FILE, true)) {   // true = append
            out.write(action + "\n");
        } catch (IOException e) {
            System.out.println("Could not write to the log file.");
        }
    }
}
