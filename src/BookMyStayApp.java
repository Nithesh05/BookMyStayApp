import java.util.*;

// ---------------- RESERVATION ----------------
class Reservation {
    String guestName;
    String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// ---------------- ROOM INVENTORY ----------------
class RoomInventory {

    private final Map<String, Integer> inventory = new HashMap<>();

    RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
    }

    // ✅ synchronized method (critical section)
    public synchronized boolean allocateRoom(String roomType) {

        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// ---------------- BOOKING PROCESSOR (THREAD) ----------------
class BookingProcessor extends Thread {

    private final Queue<Reservation> queue;
    private final RoomInventory inventory;

    BookingProcessor(Queue<Reservation> queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation reservation;

            // ✅ synchronized queue access
            synchronized (queue) {
                if (queue.isEmpty()) {
                    break;
                }
                reservation = queue.poll();
            }

            // Try booking
            boolean success = inventory.allocateRoom(reservation.roomType);

            if (success) {
                System.out.println(Thread.currentThread().getName() +
                        " booked " + reservation.guestName +
                        " (" + reservation.roomType + ")");
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " failed for " + reservation.guestName +
                        " (" + reservation.roomType + ")");
            }
        }
    }
}

// ---------------- MAIN ----------------
public class BookMyStayApp
{
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        // Shared queue
        Queue<Reservation> queue = new LinkedList<>();

        // Simulate multiple users
        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Single Room"));
        queue.add(new Reservation("Charlie", "Single Room"));
        queue.add(new Reservation("David", "Double Room"));
        queue.add(new Reservation("Eve", "Double Room"));

        // Create multiple threads
        Thread t1 = new BookingProcessor(queue, inventory);
        Thread t2 = new BookingProcessor(queue, inventory);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inventory.displayInventory();
    }
}