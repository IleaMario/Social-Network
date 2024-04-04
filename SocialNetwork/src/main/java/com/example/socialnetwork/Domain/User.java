package com.example.socialnetwork.Domain;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;


    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User U)) return false;
        return getFirstName().equals(U.getFirstName()) &&
                getLastName().equals(U.getLastName());
    }



    @Override
    public String toString() {
        return "User{" + "FirstName='" + firstName + '\'' + ", LastName='" + lastName + '\'' + "ID="+id +'}';
    }
}
