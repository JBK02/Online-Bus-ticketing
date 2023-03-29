package application.model;

import application.Enum.Gender;

public class Passenger {
    public String Name;
    public String DOB;
    public Gender gender;

    public Passenger(String Name, String DOB, Gender gender) {
        this.Name = Name;
        this.DOB = DOB;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "\n\tPassenger:" +
                "\n\t userName = " + Name +
                "\n\t DOB      = " + DOB  +
                "\n\t gender   = " + gender;
    }
}
