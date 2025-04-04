package com.example.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginregiseterpage extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregiseterpage);


        bottomNavigationView=findViewById(R.id.loginregisterSelector);
        if(savedInstanceState==null){
            //set default fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loginregisterContainer,signin.newInstance("xyz","xyz"))
                    .commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              if(item.getItemId()==R.id.login){
                  //load login fragment
                  getSupportFragmentManager()
                          .beginTransaction()
                          .replace(R.id.loginregisterContainer,signin
                                  .newInstance("xyz","xyz"))
                          .commit();
              }
              if(item.getItemId()==R.id.register){
                  getSupportFragmentManager().beginTransaction()
                          .replace(R.id.loginregisterContainer
                          ,signup.newInstance("xyz","xyz") ).commit();
              }
                return true;
            }
        });
    }
}