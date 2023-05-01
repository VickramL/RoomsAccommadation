import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private int capacity;
    private List<Person> occupants;

    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.occupants = new ArrayList<>();
    }

    public boolean isFull() {
        return capacity == occupants.size();
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Person> getOccupants() {
        return occupants;
    }

    public void setOccupants(List<Person> occupants) {
        this.occupants = occupants;
    }

    public int getNumberOfOccupants() {
        return occupants.size();
    }
}
