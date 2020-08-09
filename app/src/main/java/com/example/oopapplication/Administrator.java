package com.example.oopapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Administrator extends AppCompatActivity implements iManageEditDelete, iManageAdd {

    private Button button;
    private String Name;
    private int Age;
    private String ID;

    public Administrator(){};

    public Administrator(String name, int age, String id){
        this.Name = name;
        this.Age = age;
        this.ID = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartPreview();
            }
        });
    }

    public void StartPreview(){
        Intent intent = new Intent(this, Preview.class);
        startActivity(intent);
    }

    @Override
    public void Add() {
        //add the profile information into the database
        System.out.println ("ADDING FROM ADMIN");
    }

    @Override
    public void Edit() {
        //edit the profile information in the database
        System.out.println ("EDITING FROM ADMIN");
    }

    @Override
    public void Delete() {
        //delete the profile information in the database
        System.out.println ("DELETING FROM ADMIN");
    }
}