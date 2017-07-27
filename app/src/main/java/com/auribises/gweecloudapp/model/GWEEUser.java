package com.auribises.gweecloudapp.model;

import java.io.Serializable;

/**
 * Created by ishantkumar on 21/07/17.
 */

public class GWEEUser implements Serializable{

    int id;
    String name;
    String email;
    String password;
    String gender;
    String city;

    public GWEEUser(){
        id = 0;
        name = "";
        email = "";
        password = "";
        gender = "";
        city = "";
    }

    public GWEEUser(int id, String name, String email, String password, String gender, String city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "\nId: " + id +
                "\n\nName: " + name +
                "\n\nEmail: " + email +
                "\n\nPassword: " + password +
                "\n\nGender: " + gender +
                "\n\nCity: " + city;
    }
}
