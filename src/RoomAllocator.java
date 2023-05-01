import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class RoomAllocator {
    private static int perNo = 1;
//    private static char roomNumber = 'A';
    volatile private List<Person> personsList = new ArrayList<>();
    volatile private List<Room> neededMaintanence = new ArrayList<>();
    volatile private Map<String, Room> rooms = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        RoomAllocator room = new RoomAllocator();
        System.out.println("1. Sequential\n2. Parallel");
        System.out.print("Choose your option : ");
        int option;
        try{
            option = input.nextInt();
            if(option == 1){
                room.sequential();
            } else if (option == 2) {
                room.parallel();
            }
            else {
                System.out.println("else Invalid Input");
            }
        }
        catch (Exception e){
            System.out.println("exception Invalid Input");
        }
        // room.sequential();
//        room.parallel();
    }

    private void sequential() {
        initialise(10);
        allocate(30);

        allocate(5);

        neededMaintanence.add(rooms.get("Room C"));
        neededMaintanence.add(rooms.get("Room F"));

        doMaintanence(neededMaintanence);

        addRoom("Room K", 1);

        vacate(25);

    }

//    private

    private void vacate(int number) {
        if(personsList.size()<number) number = personsList.size();
        System.out.println("going to vacate " + number + " persons");
        List<Person> vacated = new ArrayList<>(personsList.subList(0, number));
        personsList = personsList.subList(number, personsList.size());
//        for(Person vacatedPerson:vacated){
//            String name = vacatedPerson.getRoomName();
//            rooms.get(name).getOccupants().remove(vacatedPerson);
//        }

        for (Room room : rooms.values()) {
            List<Person> occupant = room.getOccupants();
            for (int i = 0; i < occupant.size(); i++) {
                if (vacated.contains(occupant.get(i))) {
//                    System.out.println(room.getName() + "\t" + occupant.get(i).getName());
//                    removeValue(vacated, occupant, i);
                    vacated.remove(occupant.remove(i));
                    i--;
                }
            }
        }

        for (Room room : rooms.values()) {
            System.out.println(room.getName() + "\t" + room.getNumberOfOccupants());
            for (Person p : room.getOccupants())
                System.out.println(p.getName());
            System.out.println("___");
        }

        System.out.println("_________\n");

        redistribute();

        System.out.println(number + " persons vacatted successfully");
        System.out.println(personsList.size());
    }

    private void addRoom(String name, int capacity) {
        System.out.println("adding "+name+" with capacity "+capacity);
        Room newRoom = new Room(name, capacity);

        rooms.put(name, newRoom);

        redistribute();
        System.out.println("added "+name+" with capacity "+capacity + " successfully");
    }

    private void redistribute() {
        System.out.println("persons redistributing ...");
        int maintainable = personsList.size() / rooms.size();
//        System.out.println(maintainable);
        List<Person> extraList = new ArrayList<>();
        for (Room room : rooms.values()) {
            int dif = room.getNumberOfOccupants() - maintainable;

            if (dif <= 0) continue;
            List<Person> occupants = room.getOccupants();
            while (dif-- > 0) {
                Person extra = occupants.remove(occupants.size() - 1);
                extraList.add(extra);
            }
        }
        allocate(extraList);
        System.out.println("going to redistribute");
        System.out.println(personsList.size());
    }

    private void doMaintanence(List<Room> neededMaintanence) {
        System.out.println("rooms under maintanence");
        List<Person> needToShift = new ArrayList<>();
        for (Room maintain : neededMaintanence) {

            needToShift.addAll(maintain.getOccupants());
            maintain.getOccupants().clear();

            rooms.remove(maintain.getName());

        }

        allocate(needToShift);

    }

    void initialise(int numRooms) {
        for (int i = 0; i < numRooms; i++) {
            String key = "Room " + (char) ('A' + i);
            rooms.put(key, new Room(key, 10));
        }

    }

    synchronized void allocate(int numPeople) {
        System.out.println("allocating rooms for "+numPeople+" persons");
        Queue<Room> ordedRooms = new PriorityQueue<>((a, b) -> {
            int diff = a.getNumberOfOccupants() - b.getNumberOfOccupants();
            if (diff == 0)
                return a.getName().compareTo(b.getName());
            return diff;
        });

        ordedRooms.addAll(rooms.values());

        while (numPeople > 0) {
            Room room = ordedRooms.poll();

            if (room.isFull())
                continue;
            personsList.add(new Person("Person " + (perNo++),room.getName()));
            room.getOccupants().add(personsList.get(personsList.size() - 1));
            ordedRooms.add(room);

            numPeople--;
        }
        System.out.println("allocated successfully");

        for (Room room : rooms.values())
            System.out.println(room.getName() + "\t" + room.getNumberOfOccupants());

        System.out.println("_________\n");
        System.out.println(personsList.size());

    }

    synchronized void allocate(List<Person> needTo) {
//        System.out.println("");
        Queue<Room> sortedRooms = new PriorityQueue<>((a, b) -> {
            int diff = a.getNumberOfOccupants() - b.getNumberOfOccupants();
            if (diff == 0)
                return a.getName().compareTo(b.getName());
            return diff;
        });

        sortedRooms.addAll(rooms.values());

        int numPeople = 0;

        while (numPeople < needTo.size()) {
            Room room = sortedRooms.poll();

            if (room.isFull())
                continue;
            Person person = needTo.get(numPeople);
            person.setRoomName(room.getName());
            room.getOccupants().add(person);

            sortedRooms.add(room);

            numPeople++;
        }

        for (Room room : rooms.values())
            System.out.println(room.getName() + "\t" + room.getNumberOfOccupants());

        System.out.println("_________\n");
        System.out.println(personsList.size());

    }

    public synchronized void removeValue(List<Person> vacted, List<Person> occupant, int index){
        vacted.remove(occupant.remove(index));
    }

    public void parallel() {
        initialise(10);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                allocate(30);
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                allocate(5);
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                neededMaintanence.add(rooms.get("Room C"));
                neededMaintanence.add(rooms.get("Room F"));

                doMaintanence(neededMaintanence);
            }
        });

        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                addRoom("Room K", 1);
            }
        });

        Thread thread5 = new Thread(new Runnable() {
            @Override
            public void run() {
//                while (personsList.size() < 27) {
//                }

                vacate(17);
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
    }
}
