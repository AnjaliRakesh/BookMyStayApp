import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        // ================= UC1 =================
        System.out.println("=================================");
        System.out.println("       BOOK MY STAY APP          ");
        System.out.println("   Hotel Booking Management      ");
        System.out.println("           Version 6.0           ");
        System.out.println("=================================");

        // ================= UC2 =================
        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        // ================= UC3 =================
        RoomInventory inventory = new RoomInventory();

        // ================= UC4 =================
        System.out.println("\nAvailable Rooms:");
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
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        requestQueue.addRequest(new Reservation("Anjali", "Single"));
        requestQueue.addRequest(new Reservation("Rahul", "Double"));
        requestQueue.addRequest(new Reservation("Priya", "Suite"));

        // ================= UC6 =================
        BookingService bookingService = new BookingService(inventory);

        System.out.println("\nProcessing Booking Requests:");

        while (!requestQueue.isEmpty()) {
            Reservation r = requestQueue.getNextRequest();
            bookingService.confirmReservation(r);
        }
    }
}


// ==================================================
// UC2 — Abstract Room
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
// UC2 — Room Types
// ==================================================

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 2000); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 3500); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 6000); }
}


// ==================================================
// UC3 — Inventory
// ==================================================

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decreaseAvailability(String roomType) {
        inventory.put(roomType, getAvailability(roomType) - 1);
    }
}


// ==================================================
// UC5 — Reservation
// ==================================================

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}


// ==================================================
// UC5 — Booking Request Queue
// ==================================================

class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}


// ==================================================
// UC6 — Booking Service (Allocation)
// ==================================================

class BookingService {

    private RoomInventory inventory;

    // Prevent duplicate room IDs
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Map room type → assigned IDs
    private HashMap<String, Set<String>> allocationMap = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void confirmReservation(Reservation r) {

        String type = r.getRoomType();

        if (inventory.getAvailability(type) <= 0) {
            System.out.println("No available " + type +
                    " rooms for " + r.getGuestName());
            return;
        }

        // Generate unique ID
        String roomId = generateUniqueRoomId(type);

        // Record allocation
        allocatedRoomIds.add(roomId);

        allocationMap
                .computeIfAbsent(type, k -> new HashSet<>())
                .add(roomId);

        // Update inventory
        inventory.decreaseAvailability(type);

        System.out.println("Reservation confirmed for "
                + r.getGuestName()
                + " | Room Type: " + type
                + " | Room ID: " + roomId);
    }

    private String generateUniqueRoomId(String type) {

        String prefix = type.substring(0, 1).toUpperCase();

        String id;

        do {
            id = prefix + (100 + new Random().nextInt(900));
        }
        while (allocatedRoomIds.contains(id));

        return id;
    }
}