import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=========== BOOK MY STAY APP (v8.0) ==========");

        // Rooms
        Room single = new SingleRoom();
        Room dbl = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Inventory
        RoomInventory inventory = new RoomInventory();

        // Display available rooms
        System.out.println("\nAvailable Rooms:");
        Room[] rooms = { single, dbl, suite };

        for (Room r : rooms) {
            int available = inventory.getAvailability(r.getTypeKey());
            if (available > 0) {
                r.displayDetails();
                System.out.println("Available: " + available);
            }
        }

        // Booking requests
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Anjali", "Single"));
        queue.addRequest(new Reservation("Rahul", "Double"));
        queue.addRequest(new Reservation("Priya", "Suite"));

        BookingService bookingService = new BookingService(inventory);
        BookingHistory history = new BookingHistory();

        System.out.println("\nProcessing Bookings:");

        while (!queue.isEmpty()) {
            Reservation r = queue.getNextRequest();
            ConfirmedReservation confirmed =
                    bookingService.confirmReservation(r);

            if (confirmed != null) {
                history.addReservation(confirmed);   // UC8 storage
            }
        }

        // Admin views history
        System.out.println("\n--- Booking History ---");
        history.displayHistory();

        // Admin generates report
        System.out.println("\n--- Booking Report ---");
        BookingReportService reportService =
                new BookingReportService(history);

        reportService.generateSummary();
    }
}


// ==================================================
// Room Classes (UC2)
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
// Inventory (UC3)
// ==================================================

class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decreaseAvailability(String type) {
        inventory.put(type, getAvailability(type) - 1);
    }
}


// ==================================================
// Reservation + Queue (UC5)
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

class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) { queue.add(r); }
    public Reservation getNextRequest() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}


// ==================================================
// Confirmed Reservation (UC6)
// ==================================================

class ConfirmedReservation {

    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;

    public ConfirmedReservation(String reservationId,
                                String guestName,
                                String roomType,
                                String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }

    public void display() {
        System.out.println("Reservation: " + reservationId +
                " | Guest: " + guestName +
                " | Room: " + roomId +
                " (" + roomType + ")");
    }
}


// ==================================================
// Booking Service (UC6)
// ==================================================

class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedIds = new HashSet<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public ConfirmedReservation confirmReservation(Reservation r) {

        String type = r.getRoomType();

        if (inventory.getAvailability(type) <= 0) {
            System.out.println("No rooms available for "
                    + r.getGuestName());
            return null;
        }

        String roomId = generateUniqueId(type);
        inventory.decreaseAvailability(type);

        String reservationId = "RES" + new Random().nextInt(1000);

        System.out.println("Confirmed: " + r.getGuestName()
                + " | " + roomId
                + " | ID: " + reservationId);

        return new ConfirmedReservation(
                reservationId,
                r.getGuestName(),
                type,
                roomId);
    }

    private String generateUniqueId(String type) {

        String prefix = type.substring(0, 1).toUpperCase();
        String id;

        do {
            id = prefix + (100 + new Random().nextInt(900));
        } while (allocatedIds.contains(id));

        allocatedIds.add(id);
        return id;
    }
}


// ==================================================
// UC8 — Booking History
// ==================================================

class BookingHistory {

    private List<ConfirmedReservation> history = new ArrayList<>();

    public void addReservation(ConfirmedReservation r) {
        history.add(r);
    }

    public List<ConfirmedReservation> getHistory() {
        return history;
    }

    public void displayHistory() {
        for (ConfirmedReservation r : history) {
            r.display();
        }
    }
}


// ==================================================
// UC8 — Reporting Service
// ==================================================

class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void generateSummary() {

        List<ConfirmedReservation> list = history.getHistory();

        System.out.println("Total Bookings: " + list.size());

        Map<String, Integer> countByType = new HashMap<>();

        for (ConfirmedReservation r : list) {
            countByType.put(
                    r.getRoomType(),
                    countByType.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("Bookings by Room Type:");
        for (String type : countByType.keySet()) {
            System.out.println(type + ": " + countByType.get(type));
        }
    }
}