//  COMMUNITY LIBRARY MANAGEMENT SYSTEM 

// Module 1: Scanner is used for reading typed input
import java.util.Scanner;

public class CommunityLibrary {

    //  MODULE 1: CONSTANTS
    static final int MAX_BOOKS = 50;   // the library can store at most 50 books
    static final int AVAILABLE = 0;    
    static final int ISSUED = 1;       

    //  MODULE 3: 1D ARRAYS (one array for each piece of book information) 
    static int[] bookIds = new int[MAX_BOOKS];
    static String[] bookNames = new String[MAX_BOOKS];
    static String[] authors = new String[MAX_BOOKS];
    static int[] categories = new int[MAX_BOOKS];      
    static boolean[] issued = new boolean[MAX_BOOKS];  

    // Module 3: array initialised with values
    static String[] categoryNames = {"Fiction", "Non-Fiction", "Children", "Reference"};

    // Module 1: variable that remembers how many books are stored right now
    static int count = 0;

    //  MODULE 1: Scanner object for console input 
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        boolean running = true;

        System.out.println("Welcome to the Community Library!");

        //  MODULE 2: WHILE LOOP AND SWITCH 
        while (running) {
            displayMenu();
            choice = readInt("Enter your choice: ");

            // Module 2: switch statement for the menu
            switch (choice) {
                case 1:
                    addBooks();
                    break;
                case 2:
                    displayBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    issueBook();
                    break;
                case 5:
                    returnBook();
                    break;
                case 6:
                    showStatistics();
                    break;
                case 7:
                    System.out.println("Thank you for using the Community Library Management System. Goodbye!");
                    running = false;   
                    break;
                default:
                    // Module 2: default runs when no case matches
                    System.out.println("Invalid choice! Please enter a number from 1 to 7.");
            }
        }

        sc.close();
    }

    //  MODULE 3: METHOD WITHOUT PARAMETERS 
    static void displayMenu() {
        System.out.println();
        System.out.println("===== COMMUNITY LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Book");
        System.out.println("2. Display Books");
        System.out.println("3. Search Book");
        System.out.println("4. Issue Book");
        System.out.println("5. Return Book");
        System.out.println("6. Library Statistics");
        System.out.println("7. Exit");
    }

    // MODULE 3: METHOD WITH PARAMETER AND RETURN VALUE
    static int readInt(String message) {
        System.out.print(message);

        // Module 2: while loop for basic input validation
        // Module 1: ! is the logical NOT operator
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a whole number: ");
            sc.next();   
        }

        int number = sc.nextInt();
        sc.nextLine();   
        return number;
    }

    // MODULE 3: LINEAR SEARCH + METHOD OVERLOADING 
    static int findBook(int id) {
        // Module 3: array traversal using a for loop
        for (int i = 0; i < count; i++) {
            // Module 1: == relational operator
            if (bookIds[i] == id) {
                return i;   // found, stop searching
            }
        }
        return -1;   // checked every book, not found
    }

    static int findBook(String name) {
        for (int i = 0; i < count; i++) {
            if (bookNames[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    //  FEATURE 1: ADD BOOKS 
    // Module 3: method that calls another method in a loop
    static void addBooks() {
        int howMany = readInt("How many books do you want to add? ");

        // Module 2: if condition
        if (howMany <= 0) {
            System.out.println("Please enter a number greater than 0.");
            return;
        }

        int added = 0;   // Module 3: counting technique

        // Module 2: for loop to add several books one after another
        for (int i = 1; i <= howMany; i++) {
            if (count == MAX_BOOKS) {
                System.out.println("The library is full. No more books can be added.");
                break;   // Module 2: break out of the loop early
            }

            System.out.println();
            System.out.println("--- Book " + i + " of " + howMany + " ---");

            // Module 3: using the boolean return value of addBook()
            if (addBook()) {
                added++;   // Module 1: unary increment operator
            }
        }

        System.out.println();
        System.out.println(added + " book(s) added. Total books in library: " + count);
    }

   
    static boolean addBook() {
        int id = readInt("Enter Book ID: ");

        // Module 2: if-else-if style checks for basic validation
        if (id <= 0) {
            System.out.println("Book ID must be a positive number. Book not added.");
            return false;
        }
        if (findBook(id) != -1) {
            System.out.println("A book with ID " + id + " already exists. Book not added.");
            return false;
        }

        // Module 1: reading text input with Scanner
        System.out.print("Enter Book Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter Author Name: ");
        String author = sc.nextLine().trim();

        // Module 1: || logical OR operator
        if (name.length() == 0 || author.length() == 0) {
            System.out.println("Book name and author cannot be empty. Book not added.");
            return false;
        }

        // Module 3: traversing the categoryNames array to show the choices
        System.out.print("Categories: ");
        for (int i = 0; i < categoryNames.length; i++) {
            System.out.print((i + 1) + ". " + categoryNames[i] + "   ");
        }
        System.out.println();

        // Module 2: do-while loop - ask at least once, repeat if the category is wrong
        int category;
        do {
            category = readInt("Enter Category (1-" + categoryNames.length + "): ");
            if (category < 1 || category > categoryNames.length) {
                System.out.println("Invalid category, try again.");
            }
        } while (category < 1 || category > categoryNames.length);

        // Module 3: storing the details at position 'count' in every array
        bookIds[count] = id;
        bookNames[count] = name;
        authors[count] = author;
        categories[count] = category - 1;   
        issued[count] = false;              
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

        System.out.println();
        // Module 1: formatted printing with printf
        System.out.printf("%-6s %-26s %-20s %-12s %-10s%n", "ID", "Book Name", "Author", "Category", "Status");
        System.out.println("------------------------------------------------------------------------------");

        // Module 3: array traversal
        for (int i = 0; i < count; i++) {
            // Module 1: ternary operator to choose the status text
            String status = issued[i] ? "Issued" : "Available";
            System.out.printf("%-6d %-26s %-20s %-12s %-10s%n",
                    bookIds[i], bookNames[i], authors[i], categoryNames[categories[i]], status);
        }
    }

    // Module 3: method with a parameter - prints the details of one book
    static void showBookDetails(int index) {
        System.out.println("Book ID   : " + bookIds[index]);
        System.out.println("Book Name : " + bookNames[index]);
        System.out.println("Author    : " + authors[index]);
        System.out.println("Category  : " + categoryNames[categories[index]]);
        System.out.println("Status    : " + (issued[index] ? "Issued" : "Available"));
    }

    // ===== FEATURE 3: SEARCH FOR A BOOK =====
    static void searchBook() {
        if (count == 0) {
            System.out.println("No books in the library yet.");
            return;
        }

        System.out.println("Search by: 1. Book ID   2. Book Name");
        int option = readInt("Enter option: ");
        int index;

        // Module 2: if-else-if ladder
        if (option == 1) {
            int id = readInt("Enter Book ID: ");
            index = findBook(id);          
        } else if (option == 2) {
            System.out.print("Enter Book Name: ");
            String name = sc.nextLine().trim();
            index = findBook(name);        
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (index != -1) {
            System.out.println("Book found!");
            showBookDetails(index);
        } else {
            System.out.println("Book not found.");
        }
    }

    // ===== FEATURE 4: ISSUE A BOOK =====
    static void issueBook() {
        int id = readInt("Enter Book ID to issue: ");
        int index = findBook(id);   // Module 3: linear search

        // Module 2: if-else-if ladder with three possible results
        if (index == -1) {
            System.out.println("No book found with ID " + id + ".");
        } else if (issued[index]) {
            System.out.println("Sorry, \"" + bookNames[index] + "\" is already issued.");
        } else {
            issued[index] = true;   // Module 1: assignment operator
            System.out.println("\"" + bookNames[index] + "\" has been issued successfully.");
        }
    }

    // ===== FEATURE 5: RETURN A BOOK =====
    static void returnBook() {
        int id = readInt("Enter Book ID to return: ");
        int index = findBook(id);

        if (index == -1) {
            System.out.println("No book found with ID " + id + ".");
        } else if (!issued[index]) {
            System.out.println("\"" + bookNames[index] + "\" is already available. It was not issued.");
        } else {
            issued[index] = false;
            System.out.println("\"" + bookNames[index] + "\" has been returned. Thank you!");
        }
    }

    //  MODULE 3: COUNTING TECHNIQUE 
  
    static int countIssued() {
        int total = 0;
        for (int i = 0; i < count; i++) {
            if (issued[i]) {
                total++;
            }
        }
        return total;
    }

    // ===== FEATURE 6: LIBRARY STATISTICS =====
    static void showStatistics() {
        if (count == 0) {
            System.out.println("No books in the library yet.");
            return;
        }

        int issuedCount = countIssued();
        int availableCount = count - issuedCount;   // Module 1: arithmetic operator

        // Module 1: typecasting 
        double issuedPercent = (double) issuedCount / count * 100;
        double averageIssued = (double) issuedCount / categoryNames.length;

        System.out.println();
        System.out.println("===== LIBRARY STATISTICS =====");
        System.out.println("Total books     : " + count);
        System.out.println("Available books : " + availableCount);
        System.out.println("Issued books    : " + issuedCount);
        System.out.printf("Books issued    : %.1f%%%n", issuedPercent);

        // ===== MODULE 3: 2D ARRAY =====
       
        int[][] categoryTable = new int[categoryNames.length][2];

        // Module 3: counting into the 2D array
        for (int i = 0; i < count; i++) {
            int row = categories[i];
            int col = issued[i] ? ISSUED : AVAILABLE;
            categoryTable[row][col]++;
        }

        System.out.println();
        System.out.println("Category-wise report:");
        System.out.printf("%-12s %-10s %-8s %-6s%n", "Category", "Available", "Issued", "Total");

        // Module 2: nested loops
        for (int r = 0; r < categoryNames.length; r++) {
            int rowTotal = 0;
            // Module 3: enhanced for-each loop for summation of one row
            for (int value : categoryTable[r]) {
                rowTotal += value;   // Module 1: compound assignment operator
            }
            System.out.printf("%-12s %-10d %-8d %-6d%n",
                    categoryNames[r], categoryTable[r][AVAILABLE], categoryTable[r][ISSUED], rowTotal);
        }

        System.out.println();
        System.out.printf("Average issued books per category: %.2f%n", averageIssued);

        // Module 3: (searching for the maximum)
        if (issuedCount > 0) {
            int best = 0;
            for (int r = 1; r < categoryNames.length; r++) {
                if (categoryTable[r][ISSUED] > categoryTable[best][ISSUED]) {
                    best = r;
                }
            }
            System.out.println("Most borrowed category right now: " + categoryNames[best]);
        }
    }
} 
