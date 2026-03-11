import java.util.HashMap;

public class BookMyStayApp {

    public static void main(String[] args) {

        // ================= UC1 =================
        System.out.println("=================================");
        System.out.println("       BOOK MY STAY APP          ");
        System.out.println("   Hotel Booking Management      ");
        System.out.println("           Version 4.0           ");
        System.out.println("=================================");

        System.out.println("Application Started Successfully!");


        // ================= UC2 =================
        // Room Initialization (OOP)

        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();


        // ================= UC3 =================
        // Centralized Inventory (HashMap)

        RoomInventory inventory = new RoomInventory();

        System.out.println("\nRoom Details & Availability:");

        single.displayDetails();
        System.out.println("Available: " + inventory.getAvailability("Single"));

        dbl.displayDetails();
        System.out.println("Available: " + inventory.getAvailability("Double"));

        suite.displayDetails();
        System.out.println("Available: " + inventory.getAvailability("Suite"));

        inventory.displayInventory();


        // ================= UC4 =================
        // Room Search & Availability Check (READ-ONLY)

        System.out.println("\nAvailable Rooms for Booking:");

        Room[] rooms = { single, dbl, suite };

        for (Room room : rooms) {

            String key = room.getTypeKey();
            int available = inventory.getAvailability(key);

            if (available > 0) {   // Show only available rooms
                room.displayDetails();
                System.out.println("Available: " + available);
            }
        }
    }
}


// ==================================================
// UC2 — Abstract Room Class
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

    // Used by UC4 search logic
    public String getTypeKey() {
        if (type.contains("Single")) return "Single";
        if (type.contains("Double")) return "Double";
        return "Suite";
    }
}


// ==================================================
// UC2 — Concrete Room Types
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


// ==================================================
// UC3 — Centralized Room Inventory
// ==================================================

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single", 10);
        inventory.put("Double", 5);
        inventory.put("Suite", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void displayInventory() {

        System.out.println("\nCurrent Inventory:");

        for (String type : inventory.keySet()) {
            System.out.println(type + " Rooms Available: " + inventory.get(type));
        }
    }
}