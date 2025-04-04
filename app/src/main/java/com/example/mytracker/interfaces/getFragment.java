package com.example.mytracker.interfaces;

import com.example.mytracker.adapters.savedlocAdapter;
import com.example.mytracker.adapters.vehiclelistAdapter;

public interface getFragment {
    public void getFragmentOne(vehiclelistAdapter adapter, int which);
    public void getFragmentTwo(savedlocAdapter adapter , int which);
}
