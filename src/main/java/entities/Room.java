package entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Room {
    @Id
    @GeneratedValue
    private int id;
    private int roomNumber;
    private int adultsCapacity;
    private int childrenCapacity;
    private float price;

    public Room(){
    }

    public Room(int roomNumber, int adultsCapacity, int childrenCapacity, float price){
        this.roomNumber = roomNumber;
        this.adultsCapacity = adultsCapacity;
        this.childrenCapacity = childrenCapacity;
        this.price = price;
    }

    public Room(int id, int roomNumber, int adultsCapacity, int childrenCapacity, float price){
        this.id = id;
        this.roomNumber = roomNumber;
        this.adultsCapacity = adultsCapacity;
        this.childrenCapacity = childrenCapacity;
        this.price = price;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getAdultsCapacity() {
        return adultsCapacity;
    }

    public void setAdultsCapacity(int adultsCapacity) {
        this.adultsCapacity = adultsCapacity;
    }

    public int getChildrenCapacity() {
        return childrenCapacity;
    }

    public void setChildrenCapacity(int childrenCapacity) {
        this.childrenCapacity = childrenCapacity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomNumber == room.roomNumber && adultsCapacity == room.adultsCapacity && childrenCapacity == room.childrenCapacity && Float.compare(price, room.price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber, adultsCapacity, childrenCapacity, price);
    }

    @Override
    public String toString() {
        return String.valueOf(roomNumber);
    }
}
