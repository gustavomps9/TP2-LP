package entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Status {
    @Id
    private int id;
    private String state;

    public Status() {
    }

    public Status(String state) {
        this.state = state;
    }

    public Status(int id, String state) {
        this.id = id;
        this.state = state;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
