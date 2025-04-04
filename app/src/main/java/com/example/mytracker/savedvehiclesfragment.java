package com.example.mytracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mytracker.adapters.savedlocAdapter;
import com.example.mytracker.interfaces.displayGoogleMaps;
import com.example.mytracker.interfaces.updateAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link savedvehiclesfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class savedvehiclesfragment extends Fragment implements updateAdapter, displayGoogleMaps {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView savedLocList;
    private Toolbar toolbar;
    savedlocAdapter adapter;
    static public ArrayList<DataSnapshot> availableSavedVehicles;


    public savedvehiclesfragment() {
        // Required empty public constructor
    }
    public interface mycontext{
        public MainActivity getContext();
    };
    public mycontext mcontext;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment savedvehiclesfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static savedvehiclesfragment newInstance(String param1, String param2, ArrayList<DataSnapshot> data) {
        savedvehiclesfragment fragment = new savedvehiclesfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        availableSavedVehicles=data;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext=(mycontext) context;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_savedvehiclesfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedLocList=view.findViewById(R.id.savedLocList);
        toolbar=view.findViewById(R.id.toolbar);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter=new savedlocAdapter(availableSavedVehicles,this);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        savedLocList.setAdapter(adapter);
        savedLocList.setLayoutManager(manager);
        mcontext.getContext().whichFragment=1;
        mcontext.getContext().getFragmentTwo(adapter,1);
    }

    @Override
    public void updateAdapter() {
      adapter.notifyDataSetChanged();
    }

    @Override
    public void showGoogleMaps(String latitude, String longitude) {
        String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "current vehicle location" + ")";
        Intent Mapintent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        getActivity().startActivity(Mapintent);
    }


}