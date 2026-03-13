import java.util.HashMap;

class Room {
    String type;
    double price;

    Room(String type, double price) {
        this.type = type;
        this.price = price;
    }

    void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price per Night: $" + price);
    }
}

class RoomInventory {

    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 0);  // Example unavailable room
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public HashMap<String, Integer> getAllRooms() {
        return inventory;
    }
}

class SearchService {

    public static void searchAvailableRooms(RoomInventory inventory) {

        System.out.println("\nAvailable Rooms:\n");

        HashMap<String, Integer> rooms = inventory.getAllRooms();

        for (String type : rooms.keySet()) {

            int available = rooms.get(type);

            if (available > 0) {

                Room room;

                if (type.equals("Single Room")) {
                    room = new Room(type, 80);
                } else if (type.equals("Double Room")) {
                    room = new Room(type, 120);
                } else {
                    room = new Room(type, 250);
                }

                room.displayDetails();
                System.out.println("Available: " + available);
                System.out.println("----------------------");
            }
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("Book My Stay - Hotel Booking App");
        System.out.println("Version 4.1");
        System.out.println("=================================");

        RoomInventory inventory = new RoomInventory();

        SearchService.searchAvailableRooms(inventory);

    }
}