public class BookMyStayApp {

    public static void main(String[] args) {

        // ================= UC1 =================
        // Application Entry & Welcome Message

        System.out.println("=================================");
        System.out.println("       BOOK MY STAY APP          ");
        System.out.println("   Hotel Booking Management      ");
        System.out.println("           Version 2.0           ");
        System.out.println("=================================");

        System.out.println("Application Started Successfully!");

        // ================= UC2 =================
        // Basic Room Types & Static Availability

        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability variables
        int singleAvailable = 10;
        int doubleAvailable = 5;
        int suiteAvailable = 2;

        System.out.println("\nRoom Details & Availability:");

        single.displayDetails();
        System.out.println("Available: " + singleAvailable);

        dbl.displayDetails();
        System.out.println("Available: " + doubleAvailable);

        suite.displayDetails();
        System.out.println("Available: " + suiteAvailable);
    }
}


// ==================================================
// Abstract Room Class
// ==================================================

abstract class Room {

    protected int beds;
    protected double price;
    protected String type;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("\nRoom Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Price: ₹" + price);
    }
}


// ==================================================
// Concrete Room Types
// ==================================================

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000);
    }
}