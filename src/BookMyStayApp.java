import java.util.*;

// Add-On Service class
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

// Manager class
class AddOnServiceManager {

    // Map<ReservationID, List of Services>
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
    }

    // Calculate total cost
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

    // Display services
    public void displayServices(String reservationId) {
        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null) {
            System.out.println("No services added.");
            return;
        }

        System.out.println("Services for Reservation " + reservationId + ":");
        for (AddOnService s : services) {
            System.out.println("- " + s.getName() + " : ₹" + s.getCost());
        }
    }
}

// Main class
public class BookMyStayApp
{
    public static void main(String[] args) {

        AddOnServiceManager manager = new AddOnServiceManager();

        // Create services
        AddOnService food = new AddOnService("Food", 500);
        AddOnService spa = new AddOnService("Spa", 1500);
        AddOnService wifi = new AddOnService("WiFi", 300);

        // Add services to reservation
        manager.addService("R101", food);
        manager.addService("R101", spa);
        manager.addService("R101", wifi);

        // Display services
        manager.displayServices("R101");

        // Total cost
        double total = manager.calculateTotalCost("R101");
        System.out.println("Total Add-On Cost: ₹" + total);
    }
}