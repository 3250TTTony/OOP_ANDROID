package com.example.oopapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class Login extends AppCompatActivity {

    private Boolean Login;
    private String LoginUser;
    private String UserID;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControlPanel();
            }
        });
        if(OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"OpenCV loaded successfully",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Could not load",Toast.LENGTH_SHORT).show();
        }
    }

    public void ControlPanel(){
        Intent intent = new Intent(this, Administrator.class);
        startActivity(intent);
    }

    public Boolean Validate(){
        //validate the admin user from logging in
        System.out.println ("validating");
        return true;
    }

    public void Autologout(){
        //current user will auto logout after a while of not doing anything
        System.out.println ("auto log out");
    }

}