import java.util.*;

// ---------------- RESERVATION ----------------
class Reservation {
    String guestName;
    String roomType;
    String roomId;

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
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void incrementRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// ---------------- BOOKING SERVICE ----------------
class BookingService {

    private final Map<String, Reservation> confirmedBookings = new HashMap<>();
    private final RoomInventory inventory;
    private int counter = 1;

    BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public String confirmBooking(Reservation reservation) {

        if (inventory.getAvailability(reservation.roomType) <= 0) {
            System.out.println("No rooms available for " + reservation.guestName);
            return null;
        }

        String roomId = reservation.roomType.substring(0, 2).toUpperCase() + counter++;
        reservation.roomId = roomId;

        confirmedBookings.put(roomId, reservation);
        inventory.decrementRoom(reservation.roomType);

        System.out.println("Booked: " + reservation.guestName + " -> " + roomId);
        return roomId;
    }

    public Map<String, Reservation> getConfirmedBookings() {
        return confirmedBookings;
    }
}

// ---------------- CANCELLATION SERVICE (UC10) ----------------
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();

    public void cancelBooking(String roomId,
                              Map<String, Reservation> bookings,
                              RoomInventory inventory) {

        // Validate existence
        if (!bookings.containsKey(roomId)) {
            System.out.println("Cancellation failed: Invalid or already cancelled booking.");
            return;
        }

        Reservation reservation = bookings.get(roomId);

        // Push to stack (rollback tracking)
        rollbackStack.push(roomId);

        // Restore inventory
        inventory.incrementRoom(reservation.roomType);

        // Remove booking
        bookings.remove(roomId);

        System.out.println("Cancelled booking for "
                + reservation.guestName +
                " -> " + roomId);
    }

    public void displayRollbackStack() {
        System.out.println("Rollback Stack: " + rollbackStack);
    }
}

// ---------------- MAIN ----------------
public class BookMyStayApp
{
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);
        CancellationService cancelService = new CancellationService();

        // Bookings
        String r1 = bookingService.confirmBooking(new Reservation("Alice", "Single Room"));
        String r2 = bookingService.confirmBooking(new Reservation("Bob", "Double Room"));

        inventory.displayInventory();

        // Cancellation
        cancelService.cancelBooking(r1, bookingService.getConfirmedBookings(), inventory);

        // Invalid cancellation
        cancelService.cancelBooking("XX999", bookingService.getConfirmedBookings(), inventory);

        inventory.displayInventory();

        cancelService.displayRollbackStack();
    }
}