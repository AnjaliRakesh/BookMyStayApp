import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BookMyStayApp {

    public static void main(String[] args) {

        // ================= UC1 =================
        System.out.println("=================================");
        System.out.println("       BOOK MY STAY APP          ");
        System.out.println("   Hotel Booking Management      ");
        System.out.println("           Version 5.0           ");
        System.out.println("=================================");

        System.out.println("Application Started Successfully!");


        // ================= UC2 =================
        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();


        // ================= UC3 =================
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
        System.out.println("\nAvailable Rooms for Booking:");

        Room[] rooms = { single, dbl, suite };

        for (Room room : rooms) {

            String key = room.getTypeKey();
            int available = inventory.getAvailability(key);

            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
            }
        }


        // ================= UC5 =================
        // Booking Request Queue (FIFO)

        System.out.println("\nBooking Requests Received:");

        BookingRequestQueue requestQueue = new BookingRequestQueue();

        // Simulated guest requests
        requestQueue.addRequest(new Reservation("Anjali", "Single"));
        requestQueue.addRequest(new Reservation("Rahul", "Double"));
        requestQueue.addRequest(new Reservation("Priya", "Suite"));

        requestQueue.displayRequests();
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

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " Rooms Available: " + inventory.get(type));
        }
    }
}


// ==================================================
// UC5 — Reservation (Guest Request)
// ==================================================

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public void display() {
        System.out.println("Guest: " + guestName +
                " | Requested Room: " + roomType);
    }
}


// ==================================================
// UC5 — Booking Request Queue
// ==================================================

class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        queue.add(reservation);
    }

    public void displayRequests() {
        System.out.println("\nQueued Booking Requests (FIFO Order):");

        for (Reservation r : queue) {
            r.display();
        }
    }
}