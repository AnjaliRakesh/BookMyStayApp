import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        // ================= UC1 =================
        System.out.println("=================================");
        System.out.println("       BOOK MY STAY APP          ");
        System.out.println("   Hotel Booking Management      ");
        System.out.println("           Version 7.0           ");
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

        // ================= UC6 =================
        BookingService bookingService = new BookingService(inventory);

        System.out.println("\nProcessing Booking Requests:");

        List<ConfirmedReservation> confirmedList = new ArrayList<>();

        while (!requestQueue.isEmpty()) {
            Reservation r = requestQueue.getNextRequest();
            ConfirmedReservation c = bookingService.confirmReservation(r);
            if (c != null) confirmedList.add(c);
        }

        // ================= UC7 =================
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        System.out.println("\nAdding Services to Reservations:");

        if (!confirmedList.isEmpty()) {

            ConfirmedReservation res = confirmedList.get(0);

            serviceManager.addService(res.getReservationId(),
                    new AddOnService("Breakfast", 500));

            serviceManager.addService(res.getReservationId(),
                    new AddOnService("Airport Pickup", 1200));

            serviceManager.displayServices(res.getReservationId());
        }
    }
}


// ==================================================
// UC2 — Room Classes
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

class SingleRoom extends Room { public SingleRoom() { super("Single Room",1,2000);} }
class DoubleRoom extends Room { public DoubleRoom() { super("Double Room",2,3500);} }
class SuiteRoom extends Room { public SuiteRoom() { super("Suite Room",3,6000);} }


// ==================================================
// UC3 — Inventory
// ==================================================

class RoomInventory {

    private HashMap<String, Integer> inventory = new HashMap<>();

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
// UC5 — Reservation + Queue
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
// UC6 — Confirmed Reservation
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
}


// ==================================================
// UC6 — Booking Service
// ==================================================

class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedRoomIds = new HashSet<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public ConfirmedReservation confirmReservation(Reservation r) {

        String type = r.getRoomType();

        if (inventory.getAvailability(type) <= 0) {
            System.out.println("No rooms available for " + r.getGuestName());
            return null;
        }

        String roomId = generateUniqueRoomId(type);
        inventory.decreaseAvailability(type);

        String reservationId = "RES" + new Random().nextInt(1000);

        System.out.println("Confirmed: " + r.getGuestName()
                + " | Room: " + roomId
                + " | Reservation ID: " + reservationId);

        return new ConfirmedReservation(reservationId,
                r.getGuestName(), type, roomId);
    }

    private String generateUniqueRoomId(String type) {

        String prefix = type.substring(0, 1).toUpperCase();
        String id;

        do {
            id = prefix + (100 + new Random().nextInt(900));
        } while (allocatedRoomIds.contains(id));

        allocatedRoomIds.add(id);
        return id;
    }
}


// ==================================================
// UC7 — Add-On Service
// ==================================================

class AddOnService {

    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public double getCost() { return cost; }

    public void display() {
        System.out.println(name + " : ₹" + cost);
    }
}


// ==================================================
// UC7 — Service Manager
// ==================================================

class AddOnServiceManager {

    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {

        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public void displayServices(String reservationId) {

        List<AddOnService> list = serviceMap.get(reservationId);

        if (list == null) return;

        double total = 0;

        System.out.println("Services for Reservation " + reservationId);

        for (AddOnService s : list) {
            s.display();
            total += s.getCost();
        }

        System.out.println("Total Add-On Cost: ₹" + total);
    }
}