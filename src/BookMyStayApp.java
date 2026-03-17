import java.io.*;
import java.util.*;

// ---------------- RESERVATION ----------------
class Reservation implements Serializable {
    String guestName;
    String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// ---------------- ROOM INVENTORY ----------------
class RoomInventory implements Serializable {

    private Map<String, Integer> inventory = new HashMap<>();

    RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public void decrementRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public void displayInventory() {
        System.out.println("\nInventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// ---------------- BOOKING HISTORY ----------------
class BookingHistory implements Serializable {

    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAllReservations() {
        return history;
    }
}

// ---------------- PERSISTENCE SERVICE ----------------
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // SAVE
    public void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(inventory);
            out.writeObject(history);

            System.out.println("State saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // LOAD
    public Object[] load() {

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            RoomInventory inventory = (RoomInventory) in.readObject();
            BookingHistory history = (BookingHistory) in.readObject();

            System.out.println("State loaded successfully.");

            return new Object[]{inventory, history};

        } catch (FileNotFoundException e) {
            System.out.println("No saved state found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading state. Starting safely.");
        }

        // fallback (safe start)
        return new Object[]{new RoomInventory(), new BookingHistory()};
    }
}

// ---------------- MAIN ----------------
public class BookMyStayApp
{
    public static void main(String[] args) {

        PersistenceService ps = new PersistenceService();

        // LOAD previous state
        Object[] data = ps.load();
        RoomInventory inventory = (RoomInventory) data[0];
        BookingHistory history = (BookingHistory) data[1];

        // Simulate booking
        Reservation r1 = new Reservation("Alice", "Single Room");
        history.addReservation(r1);
        inventory.decrementRoom("Single Room");

        Reservation r2 = new Reservation("Bob", "Double Room");
        history.addReservation(r2);
        inventory.decrementRoom("Double Room");

        // Display
        inventory.displayInventory();

        System.out.println("\nBooking History:");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r.guestName + " -> " + r.roomType);
        }

        // SAVE state before exit
        ps.save(inventory, history);
    }
}