package com.example.oopapplication;

import java.util.ArrayList;

public class User implements iManageAdd{

    private ArrayList ProfilePictures;
    private String Name;
    private int Age;
    private String ID;

    public User(String name, int age){
        this.Name = name;
        this.Age = age;
    }

    public User(String name, int age, String id){
        this.Name = name;
        this.Age = age;
        this.ID = id;
    }

    @Override
    public void Add() {
        //add the profile information into the database
        System.out.println ("ADDING FROM USER");
    }
}
