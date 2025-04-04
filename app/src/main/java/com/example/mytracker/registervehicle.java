package com.example.mytracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mytracker.models.vehicleDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registervehicle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registervehicle extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public registervehicle() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment registervehicle.
     */
    // TODO: Rename and change types and number of parameters
    public static registervehicle newInstance(String param1, String param2) {
        registervehicle fragment = new registervehicle();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_registervehicle, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.addvehicle, null);
        EditText regNo = view.findViewById(R.id.regNo);
        EditText phoneNo = view.findViewById(R.id.phoneNo);
        Spinner vehicleType = view.findViewById(R.id.vehicleType);
        final String[] itemSelected = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(true)
                .setTitle("Register New Vehicle")
                .setPositiveButton("register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //add vehicle to the database

                        itemSelected[0] = vehicleType.getSelectedItem().toString();
                        ProgressDialog dialog1 = new ProgressDialog(getActivity());
                        dialog1.setMessage("registering this vehicle ...please wait");
                        dialog1.setTitle("vehicle registration");
                        vehicleDetails vehicledetails = new vehicleDetails();
                        vehicledetails.setVehiclePhoneNo(phoneNo.getText().toString().trim());
                        vehicledetails.setVehicleRegNo(regNo.getText().toString().trim());
                        vehicledetails.setVehicleType(itemSelected[0]);
                        String user = Objects.requireNonNull(FirebaseAuth.getInstance()
                                        .getCurrentUser())
                                .getUid();
                        DatabaseReference root = FirebaseDatabase
                                .getInstance().getReference();
                        dialog1.show();
                        DatabaseReference currentUserRef = root
                                .child("registeredVehicles").child(user);
                        String vehicleKey = currentUserRef.push().getKey();
                        assert vehicleKey != null;
                        currentUserRef.child(vehicleKey)
                                .setValue(vehicledetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog1.hide();
                                            assert getFragmentManager() != null;
                                            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer,registeredvehicles.newInstance("xyz","xyz"))
                                                            .commit();
                                            Toast.makeText(getActivity(), "vehicle registered successfully", Toast.LENGTH_LONG).show();
                                        } else {
                                            dialog1.hide();
                                            Toast.makeText(getActivity(), "failed register vehicle", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                    }
                }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//cance the dialog
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer,registeredvehicles.newInstance("xyz","xyz"))
                                        .commit();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
