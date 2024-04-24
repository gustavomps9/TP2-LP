package entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    private int id;
    private String guestFirstName;
    private String guestLastName;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    public Booking() {
    }

    public Booking(String guestFirstName, String guestLastName, int numberOfAdults, int numberOfChildren, Room room, Status status) {
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.room = room;
        this.status = status;
    }

    public Booking(String guestFirstName, String guestLastName, Date checkInDate, Date checkOutDate, int numberOfAdults, int numberOfChildren, Room room, Status status) {
        this.id = id;
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.room = room;
        this.status = status;
    }

    public Booking(int id, String guestFirstName, String guestLastName, Date checkInDate, Date checkOutDate, int numberOfAdults, int numberOfChildren, Room room, Status status) {
        this.id = id;
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.room = room;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public void setGuestFirstName(String guestFirstName) {
        this.guestFirstName = guestFirstName;
    }

    public String getGuestLastName() {
        return guestLastName;
    }

    public void setGuestLastName(String guestLastName) {
        this.guestLastName = guestLastName;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public Room getRoom() {
        return room;
    }

    public int getRoomId() {
        return room.getId();
    }

    public void setRoomId(Room room) {
        this.room = room;
    }

    public Status getStatus() {
        return status;
    }

    public int getStatusId() {
        return status.getId();
    }

    public void setStatusId(Status status) {
        this.status = status;
    }
}
