import java.util.*;

// ---------------- CUSTOM EXCEPTION ----------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

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
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, -1);
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public void decrementRoom(String roomType) {
        int count = inventory.getOrDefault(roomType, 0);

        if (count <= 0) {
            throw new RuntimeException("Invalid inventory state!");
        }

        inventory.put(roomType, count - 1);
    }
}

// ---------------- VALIDATOR ----------------
class BookingValidator {

    public static void validate(Reservation reservation, RoomInventory inventory)
            throws InvalidBookingException {

        // Validate guest name
        if (reservation.guestName == null || reservation.guestName.isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (!inventory.isValidRoomType(reservation.roomType)) {
            throw new InvalidBookingException("Invalid room type: " + reservation.roomType);
        }

        // Validate availability
        if (inventory.getAvailability(reservation.roomType) <= 0) {
            throw new InvalidBookingException("No rooms available for " + reservation.roomType);
        }
    }
}

// ---------------- BOOKING SERVICE ----------------
class BookingService {

    private final Queue<Reservation> requestQueue = new LinkedList<>();
    private final RoomInventory inventory;

    BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public void processBookings() {

        while (!requestQueue.isEmpty()) {

            Reservation reservation = requestQueue.poll();

            try {
                // ✅ Validation (UC9)
                BookingValidator.validate(reservation, inventory);

                inventory.decrementRoom(reservation.roomType);

                System.out.println("Reservation confirmed for "
                        + reservation.guestName +
                        " (" + reservation.roomType + ")");

            } catch (InvalidBookingException e) {

                // ✅ Graceful failure
                System.out.println("Booking failed for "
                        + reservation.guestName + " : " + e.getMessage());
            }
        }
    }
}

// ---------------- MAIN ----------------
public class BookMyStayApp
{
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Valid bookings
        bookingService.addRequest(new Reservation("Alice", "Single Room"));
        bookingService.addRequest(new Reservation("Bob", "Double Room"));

        // Invalid cases (UC9 test)
        bookingService.addRequest(new Reservation("", "Single Room")); // empty name
        bookingService.addRequest(new Reservation("Eve", "Luxury Room")); // invalid type
        bookingService.addRequest(new Reservation("Charlie", "Suite Room"));
        bookingService.addRequest(new Reservation("David", "Suite Room")); // no availability

        bookingService.processBookings();
    }
}