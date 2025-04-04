package com.example.mytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.navigation.NavigationView;

public class welcomepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            Intent startMainPage=new Intent(welcomepage.this,loginregiseterpage.class);
            startActivity(startMainPage);
            }
        },2000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startMainPage=new Intent(welcomepage.this,loginregiseterpage.class);
                startActivity(startMainPage);
            }
        },1500);

    }
}