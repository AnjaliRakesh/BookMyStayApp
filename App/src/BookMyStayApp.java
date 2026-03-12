import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("====== BOOK MY STAY APP (v10.0) ======");

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);
        BookingHistory history = new BookingHistory();
        CancellationService cancellationService =
                new CancellationService(inventory, history);

        // Create booking requests
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Anjali", "Single"));
        queue.addRequest(new Reservation("Rahul", "Double"));

        List<ConfirmedReservation> confirmedList = new ArrayList<>();

        System.out.println("\nProcessing Bookings:");

        while (!queue.isEmpty()) {
            Reservation r = queue.getNextRequest();
            try {
                ConfirmedReservation c =
                        bookingService.confirmReservation(r);

                if (c != null) {
                    history.addReservation(c);
                    confirmedList.add(c);
                }

            } catch (InvalidBookingException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        // UC10 — Cancel first reservation
        if (!confirmedList.isEmpty()) {

            ConfirmedReservation toCancel = confirmedList.get(0);

            System.out.println("\nCancelling Reservation:");
            cancellationService.cancelReservation(toCancel);
        }

        System.out.println("\nCurrent Inventory:");
        inventory.displayInventory();
    }
}


// ==================================================
// Custom Exception (UC9)
// ==================================================

class InvalidBookingException extends Exception {
    public InvalidBookingException(String msg) { super(msg); }
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

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decreaseAvailability(String type)
            throws InvalidBookingException {

        if (getAvailability(type) <= 0) {
            throw new InvalidBookingException(
                    "No rooms available for type: " + type);
        }

        inventory.put(type, getAvailability(type) - 1);
    }

    public void increaseAvailability(String type) {
        inventory.put(type, getAvailability(type) + 1);
    }

    public void displayInventory() {
        for (String t : inventory.keySet()) {
            System.out.println(t + ": " + inventory.get(t));
        }
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
    private boolean cancelled = false;

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
    public String getRoomId() { return roomId; }

    public boolean isCancelled() { return cancelled; }
    public void markCancelled() { cancelled = true; }

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

    public ConfirmedReservation confirmReservation(Reservation r)
            throws InvalidBookingException {

        String type = r.getRoomType();

        if (!inventory.isValidRoomType(type)) {
            throw new InvalidBookingException(
                    "Invalid room type: " + type);
        }

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

    public boolean contains(ConfirmedReservation r) {
        return history.contains(r);
    }
}


// ==================================================
// UC10 — Cancellation Service
// ==================================================

class CancellationService {

    private RoomInventory inventory;
    private BookingHistory history;

    // Stack for rollback tracking (LIFO)
    private Stack<String> releasedRoomIds = new Stack<>();

    public CancellationService(RoomInventory inventory,
                               BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancelReservation(ConfirmedReservation r) {

        // Validate existence
        if (!history.contains(r)) {
            System.out.println("Invalid cancellation: reservation not found");
            return;
        }

        // Prevent double cancellation
        if (r.isCancelled()) {
            System.out.println("Reservation already cancelled");
            return;
        }

        // Release room ID
        releasedRoomIds.push(r.getRoomId());

        // Restore inventory
        inventory.increaseAvailability(r.getRoomType());

        r.markCancelled();

        System.out.println("Cancelled reservation "
                + r.getReservationId()
                + " | Room released: " + r.getRoomId());
    }
}