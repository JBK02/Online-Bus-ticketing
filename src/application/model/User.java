package application.model;

import application.Enum.Gender;
public class User {
    public String name;
    public final String EMAIL_ID;
    public final String DOB;
    public final Gender GENDER;

    public User(String name, String EMAIL_ID, String DOB, Gender GENDER) {
        this.name = name;
        this.EMAIL_ID = EMAIL_ID;
        this.DOB = DOB;
        this.GENDER = GENDER;
    }
}
