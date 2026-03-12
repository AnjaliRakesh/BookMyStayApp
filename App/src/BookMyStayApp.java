import java.io.*;
import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("====== BOOK MY STAY APP (v12.0) ======");

        PersistenceService persistence = new PersistenceService();

        // Try to restore previous state
        SystemState state = persistence.loadState();

        RoomInventory inventory;
        BookingHistory history;

        if (state != null) {
            System.out.println("Recovered previous system state.");
            inventory = state.inventory;
            history = state.history;
        } else {
            System.out.println("Starting with fresh state.");
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        // Simulate booking
        BookingService bookingService = new BookingService(inventory);

        try {
            ConfirmedReservation c =
                    bookingService.confirmReservation(
                            new Reservation("Anjali", "Single"));

            if (c != null) history.addReservation(c);

        } catch (InvalidBookingException e) {
            System.out.println(e.getMessage());
        }

        // Save state before shutdown
        persistence.saveState(new SystemState(inventory, history));

        System.out.println("System state saved successfully.");
    }
}


// ==================================================
// Serializable System State Container
// ==================================================

class SystemState implements Serializable {

    RoomInventory inventory;
    BookingHistory history;

    public SystemState(RoomInventory inventory,
                       BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }
}


// ==================================================
// Persistence Service
// ==================================================

class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    public void saveState(SystemState state) {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(FILE_NAME))) {

            out.writeObject(state);

        } catch (IOException e) {
            System.out.println("Failed to save state.");
        }
    }

    public SystemState loadState() {

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(FILE_NAME))) {

            return (SystemState) in.readObject();

        } catch (FileNotFoundException e) {
            return null;  // First run — no file yet

        } catch (Exception e) {
            System.out.println("Corrupted data. Starting fresh.");
            return null;
        }
    }
}


// ==================================================
// Inventory (Serializable)
// ==================================================

class RoomInventory implements Serializable {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }

    public void decreaseAvailability(String type)
            throws InvalidBookingException {

        if (getAvailability(type) <= 0) {
            throw new InvalidBookingException(
                    "No rooms available for " + type);
        }

        inventory.put(type, getAvailability(type) - 1);
    }
}


// ==================================================
// Reservation
// ==================================================

class Reservation implements Serializable {

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
// Confirmed Reservation
// ==================================================

class ConfirmedReservation implements Serializable {

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
}


// ==================================================
// Booking History
// ==================================================

class BookingHistory implements Serializable {

    private List<ConfirmedReservation> history = new ArrayList<>();

    public void addReservation(ConfirmedReservation r) {
        history.add(r);
    }
}


// ==================================================
// Booking Service
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
                + " | Room: " + roomId);

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
// Custom Exception
// ==================================================

class InvalidBookingException extends Exception {
    public InvalidBookingException(String msg) { super(msg); }
}