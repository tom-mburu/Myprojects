package com.example.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.mytracker.adapters.savedlocAdapter;
import com.example.mytracker.adapters.vehiclelistAdapter;
import com.example.mytracker.interfaces.getFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements getFragment,savedvehiclesfragment.mycontext,registeredvehicles.mycontext {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    int whichFragment=0;
    savedlocAdapter locationsAdapter=null;
    vehiclelistAdapter vehiclelistAdapter=null;

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.searchbar,menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_bar));
        searchView.setQueryHint("search");
       if(whichFragment==0){
            //do search on registered vehivles
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    vehiclelistAdapter.getFilter().filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    vehiclelistAdapter.getFilter().filter(newText);
                   // Toast.makeText(MainActivity.this,"registerd vehicle search",Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }
        if(whichFragment==1){
            //do search on saved locations
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    locationsAdapter.getFilter().filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    locationsAdapter.getFilter().filter(newText);
                    return true;
                }
            });

        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //attch reference to the views
        toolbar=findViewById(R.id.toolbar);
        navigationView=findViewById(R.id.navView);
        drawerLayout=findViewById(R.id.drawerLayout);
       // setSupportActionBar(toolbar);
        toolbar.setTitle("TM Tracker");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
       // ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_menu,R.string.close_menu);
       // drawerLayout.addDrawerListener(toggle);
        toolbar.setNavigationIcon(R.drawable.humbergur);
        toolbar.
                setNavigationOnClickListener(
                        new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                                                    drawerLayout.closeDrawers();
                                                     }else{
                                                         drawerLayout.openDrawer(GravityCompat.START);
                                                     }
                                                 }
                                             }

        );
        //toggle.syncState();
        if(savedInstanceState==null){
            //set selected fragment by default

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, registeredvehicles.newInstance("xyz","xyz"))
                    .commit();
            navigationView.setCheckedItem(R.id.login);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragement=null;

if(item.getItemId()==R.id.logout){
    selectedFragement=logout.newInstance("xyz","xyz");
}
if(item.getItemId()==R.id.addvehicle){
    selectedFragement=registervehicle.newInstance("xyz","xyz");
}
if(item.getItemId()==R.id.registeredVehicle){
    selectedFragement=registeredvehicles.newInstance("xyz","xyz");
}

if(selectedFragement!=null){
    getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer,selectedFragement)
            .commit();
    selectedFragement=null;
    navigationView.setCheckedItem(item.getItemId());
    drawerLayout.closeDrawers();
}     return false;
            }
        });
    }

    @Override
    public void getFragmentOne(com.example.mytracker.adapters.vehiclelistAdapter adapter, int which) {
vehiclelistAdapter=adapter;
whichFragment=which;
    }

    @Override
    public void getFragmentTwo(savedlocAdapter adapter, int which) {
locationsAdapter=adapter;
whichFragment=which;
    }

    @Override
    public MainActivity getContext() {
        return MainActivity.this;
    }
}