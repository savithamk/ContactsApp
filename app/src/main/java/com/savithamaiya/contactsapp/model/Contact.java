package com.savithamaiya.contactsapp.model;

public class Contact implements Comparable<Contact>{
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String email;

    public Contact(String firstName, String lastName, String contactNumber,String email){
        this.contactNumber = contactNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int compareTo(Contact that) {
        if(firstName.equals(that.firstName))
            return lastName.compareTo(that.lastName);
        else
            return firstName.compareTo(that.firstName);
    }
}
