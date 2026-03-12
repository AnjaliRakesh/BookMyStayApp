import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("====== BOOK MY STAY APP (v11.0) ======");

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue queue = new BookingRequestQueue();

        // Multiple guests submit requests simultaneously
        queue.addRequest(new Reservation("Anjali", "Single"));
        queue.addRequest(new Reservation("Rahul", "Single"));
        queue.addRequest(new Reservation("Priya", "Single"));
        queue.addRequest(new Reservation("Karthik", "Single"));

        ConcurrentBookingProcessor processor =
                new ConcurrentBookingProcessor(queue, inventory);

        // Create multiple threads (simulating multiple users)
        Thread t1 = new Thread(processor);
        Thread t2 = new Thread(processor);
        Thread t3 = new Thread(processor);

        t1.start();
        t2.start();
        t3.start();
    }
}


// ==================================================
// Inventory (Shared Resource)
// ==================================================

class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single", 2);   // Only 2 rooms available
    }

    // Synchronized to prevent race conditions
    public synchronized boolean allocateRoom(String type) {

        int available = inventory.getOrDefault(type, 0);

        if (available <= 0) {
            return false;
        }

        inventory.put(type, available - 1);
        return true;
    }
}


// ==================================================
// Reservation
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
// Shared Booking Queue
// ==================================================

class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.add(r);
    }

    public synchronized Reservation getNextRequest() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}


// ==================================================
// UC11 — Concurrent Booking Processor
// ==================================================

class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;

    public ConcurrentBookingProcessor(BookingRequestQueue queue,
                                      RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation r;

            // Critical section: retrieve request
            synchronized (queue) {

                if (queue.isEmpty()) return;

                r = queue.getNextRequest();
            }

            // Critical section: allocate room
            boolean success =
                    inventory.allocateRoom(r.getRoomType());

            if (success) {
                System.out.println(Thread.currentThread().getName()
                        + " booked room for "
                        + r.getGuestName());
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " FAILED booking for "
                        + r.getGuestName()
                        + " (No rooms available)");
            }
        }
    }
}