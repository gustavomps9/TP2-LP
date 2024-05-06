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
    private int number;
    private int adultsCapacity;
    private int childrenCapacity;
    private float price;

    public Room(){
    }

    public Room(int number, int adultsCapacity, int childrenCapacity, float price){
        this.number = number;
        this.adultsCapacity = adultsCapacity;
        this.childrenCapacity = childrenCapacity;
        this.price = price;
    }

    public Room(int id, int number, int adultsCapacity, int childrenCapacity, float price){
        this.id = id;
        this.number = number;
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

    public int getNumber() {
        return number;
    }

    public void seNumber(int roomNumber) {
        this.number = roomNumber;
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
        return number == room.number && adultsCapacity == room.adultsCapacity && childrenCapacity == room.childrenCapacity && Float.compare(price, room.price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, adultsCapacity, childrenCapacity, price);
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }
}
