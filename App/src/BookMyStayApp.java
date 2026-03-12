import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("====== BOOK MY STAY APP (v9.0) ======");

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue queue = new BookingRequestQueue();

        // Valid request
        queue.addRequest(new Reservation("Anjali", "Single"));

        // Invalid room type (to show UC9 behavior)
        queue.addRequest(new Reservation("Rahul", "Deluxe"));

        BookingService bookingService = new BookingService(inventory);
        BookingHistory history = new BookingHistory();

        System.out.println("\nProcessing Bookings:");

        while (!queue.isEmpty()) {

            Reservation r = queue.getNextRequest();

            try {

                ConfirmedReservation confirmed =
                        bookingService.confirmReservation(r);

                if (confirmed != null) {
                    history.addReservation(confirmed);
                }

            } catch (InvalidBookingException e) {

                System.out.println("ERROR: " + e.getMessage());
            }
        }

        System.out.println("\nSystem continues running safely.");

        // Display history (UC8)
        System.out.println("\n--- Booking History ---");
        history.displayHistory();
    }
}


// ==================================================
// UC9 — Custom Exception
// ==================================================

class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
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
        return inventory.getOrDefault(type, -1);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }

    public void decreaseAvailability(String type)
            throws InvalidBookingException {

        int current = getAvailability(type);

        if (current <= 0) {
            throw new InvalidBookingException(
                    "No available rooms for type: " + type);
        }

        inventory.put(type, current - 1);
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

    public String getRoomType() { return roomType; }

    public void display() {
        System.out.println("Reservation: " + reservationId +
                " | Guest: " + guestName +
                " | Room: " + roomId +
                " (" + roomType + ")");
    }
}


// ==================================================
// Booking Service (UC6 + UC9 Validation)
// ==================================================

class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedIds = new HashSet<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public ConfirmedReservation confirmReservation(Reservation r)
            throws InvalidBookingException {

        String type = r.getRoomType();

        // UC9 Validation — invalid room type
        if (!inventory.isValidRoomType(type)) {
            throw new InvalidBookingException(
                    "Invalid room type requested: " + type);
        }

        // Check availability
        inventory.decreaseAvailability(type);

        String roomId = generateUniqueId(type);
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
// Booking History (UC8)
// ==================================================

class BookingHistory {

    private List<ConfirmedReservation> history = new ArrayList<>();

    public void addReservation(ConfirmedReservation r) {
        history.add(r);
    }

    public void displayHistory() {
        for (ConfirmedReservation r : history) {
            r.display();
        }
    }
}