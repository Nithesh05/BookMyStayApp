import java.util.*;

class Reservation {
    String guestName;
    String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

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

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

class BookingService {

    private final Queue<Reservation> requestQueue = new LinkedList<>();
    private final Set<String> allocatedRoomIds = new HashSet<>();
    private final Map<String, Set<String>> allocatedRooms = new HashMap<>();
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
            String roomType = reservation.roomType;

            if (inventory.getAvailability(roomType) > 0) {

                String roomId = generateRoomId(roomType);

                allocatedRoomIds.add(roomId);

                allocatedRooms
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                inventory.decrementRoom(roomType);

                System.out.println("Reservation confirmed for "
                        + reservation.guestName +
                        " -> Room ID: " + roomId);

            } else {
                System.out.println("No rooms available for "
                        + reservation.guestName +
                        " (" + roomType + ")");
            }
        }
    }

    private String generateRoomId(String roomType) {

        String prefix = roomType.substring(0, 2).toUpperCase();
        String id;

        do {
            id = prefix + (allocatedRoomIds.size() + 1);
        } while (allocatedRoomIds.contains(id));

        return id;
    }
}

// ✅ Add-On Service class
class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }
}

// ✅ Manager class
class AddOnServiceManager {

    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
    }

    public double calculateTotalCost(String reservationId) {
        double total = 0;

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services != null) {
            for (AddOnService s : services) {
                total += s.getCost();
            }
        }

        return total;
    }

    public void displayServices(String reservationId) {
        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null) {
            System.out.println("No services added.");
            return;
        }

        System.out.println("\nServices for Reservation " + reservationId + ":");
        for (AddOnService s : services) {
            System.out.println("- " + s.getName() + " : ₹" + s.getCost());
        }
    }
}

// ✅ FINAL MAIN (merged)
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("Book My Stay - Hotel Booking App");
        System.out.println("Version 7.0");
        System.out.println("=================================");

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        bookingService.addRequest(new Reservation("Alice", "Single Room"));
        bookingService.addRequest(new Reservation("Bob", "Double Room"));
        bookingService.addRequest(new Reservation("Charlie", "Suite Room"));
        bookingService.addRequest(new Reservation("David", "Single Room"));

        bookingService.processBookings();
        inventory.displayInventory();

        // ✅ Add-On Feature
        AddOnServiceManager manager = new AddOnServiceManager();

        manager.addService("R101", new AddOnService("Food", 500));
        manager.addService("R101", new AddOnService("Spa", 1500));
        manager.addService("R101", new AddOnService("WiFi", 300));

        manager.displayServices("R101");

        double total = manager.calculateTotalCost("R101");
        System.out.println("Total Add-On Cost: ₹" + total);
    }
}