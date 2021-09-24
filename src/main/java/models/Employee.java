package models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String maritalStatus;

    public Employee(Long id, String firstName, String lastName, String maritalStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maritalStatus = maritalStatus;
    }

    public Employee(){

    }
}
