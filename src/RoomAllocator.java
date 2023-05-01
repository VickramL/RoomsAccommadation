import java.util.*;

public class RoomAll {
//    List<Room> roomsList;
    Map<Integer,Room> roomList;
    List<Person> personList;
    Scanner input = new Scanner(System.in);

    public RoomAllocation(){
        this.roomList = new HashMap<>();
        this.personList = new ArrayList<>();
//        addRooms(10,10);
        for(int i = 1;i<=10;i++){
            Room room = new Room(10);
            roomList.put(room.getRoomNumber(), room);
        }
    }

    public void addPeople(){
        for(int i = 0;i<30;i++){
//            String name = "person"+(i+1);
            Person person = new Person();
            roomList.get(i%10+1).addPerson(person);
            personList.add(person);
        }
//        printRoomList();
    }

    public void addNewPeople(int count){
        int min = roomList.get(1).getCapacity()-roomList.get(1).getOccupancy(),index=0;
        for(int roomNumbers :roomList.keySet()){
            int difference = roomList.get(roomNumbers).getCapacity()-roomList.get(roomNumbers).getOccupancy();
            if(difference>min) {
                min = difference;
                index = roomNumbers -1;
                break;
            }
        }

//        int personSize = ;
        for(int i = index;i<index+count;i++){
//            String name = "person"+(personList.size()+1);
            Person person = new Person();
            personList.add(person);
            roomList.get(person.getRoomNumber()).addPerson(person);
        }
//        printRoomList();
    }

    public void doMaintainance(){
        Queue<Person> personQueue = new ArrayDeque<>();
        personQueue.addAll(roomList.get(2).getOccupants());
        personQueue.addAll(roomList.get(5).getOccupants());

        roomList.remove(2);
        roomList.remove(5);
        int min = roomList.get(1).getCapacity()-roomList.get(1).getOccupancy(),index=0;
        for(int key:roomList.keySet()){
            int difference = roomList.get(key).getCapacity()-roomList.get(key).getOccupancy();
            if(difference>min) {
                min = difference;
                index = key-1;
                break;
            }
        }
        reAllocate(index,personQueue);

//        printRoomList();
    }

    public void addRooms(int totalRooms,int capacity){
        for(int i = 1;i<=totalRooms;i++){
            Room room = new Room(capacity);
            roomList.put(room.getRoomNumber(),room);
        }
        allocatePersons();
//        printRoomList();
    }

    public void printRoomList(){
        for(int key:roomList.keySet()){
            System.out.print("room number = "+key+"  ");
            for(Person person:roomList.get(key).getOccupants())
                System.out.print(person.getName()+" ");
            System.out.println();
        }
    }

    public void allocatePersons(){
        Queue<Person> personQueue = new ArrayDeque<>();
        for(int key:roomList.keySet()){
            personQueue.addAll(roomList.get(key).getOccupants());
            roomList.get(key).clearOccupants();
        }
        reAllocate(0,personQueue);
    }

    public void reAllocate(int index,Queue<Person> personQueue){
        while(!personQueue.isEmpty()){
            int currIndex = index%(Room.getRoomNumberIncreamentor()-1)+1;
            index++;
            if(!roomList.containsKey(currIndex)) continue;
            Person person = personQueue.poll();
            roomList.get(currIndex).addPerson(person);
            person.setRoomNumber(currIndex);
        }
        printRoomList();
        System.out.println("\n\n");
    }
}
