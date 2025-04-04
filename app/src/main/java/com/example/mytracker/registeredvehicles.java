package com.example.mytracker;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytracker.adapters.vehiclelistAdapter;
import com.example.mytracker.interfaces.displayGoogleMaps;
import com.example.mytracker.interfaces.itemposition;
import com.example.mytracker.interfaces.pushNotificationOngeofenceBreak;
import com.example.mytracker.interfaces.saveCurrentLocation;
import com.example.mytracker.interfaces.updateAdapter;
import com.example.mytracker.models.curentLocationDetails;
import com.example.mytracker.models.vehicleDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registeredvehicles#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registeredvehicles extends Fragment implements updateAdapter, saveCurrentLocation, PopupMenu.OnMenuItemClickListener, itemposition, displayGoogleMaps, pushNotificationOngeofenceBreak {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private RecyclerView registeredVehiclesList;
    private ExtendedFloatingActionButton registerVehicle;
    private TextView noVehicle;
    public vehiclelistAdapter adapter;
    public ArrayList<DataSnapshot> availableVehicles;
    public int itemPosition;
    private String phoneNumber;
    private ProgressDialog requestingLocDialog;
    private static final int REQUEST_SMS = 0;
    private static final int REQ_PICK_CONTACT = 2;
    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;
    public ProgressDialog requestGpsDataDialog;
    ArrayList<DataSnapshot> savedVehicles;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
private BroadcastReceiver smsreceiver;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public registeredvehicles() {
        // Required empty public constructor
    }

    @Override
    public void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    public interface mycontext{
        public MainActivity getContext();
    };
    public mycontext mcontext;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext=(mycontext) context;


    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment registeredvehicles.
     */
    // TODO: Rename and change types and number of parameters
    public static registeredvehicles newInstance(String param1, String param2) {
        registeredvehicles fragment = new registeredvehicles();
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

       requestGpsDataDialog= new ProgressDialog(getActivity());
        requestGpsDataDialog.setTitle("Requesting gps data");
        requestGpsDataDialog.setMessage("waiting for gps coordinates...please wait");
        smsReceiver.update(requestGpsDataDialog);
        if (checkSelfPermission(getActivity(), RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        smsReceiver.getContext(this);
       // getActivity().registerReceiver(smsreceiver,new IntentFilter(SMS_RECEIVED));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registeredvehicles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registeredVehiclesList = view.findViewById(R.id.registeredVehiclesList);
        registerVehicle = view.findViewById(R.id.registerVehicle);
        noVehicle = view.findViewById(R.id.noVehicle);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        availableVehicles = new ArrayList<>();
        adapter = new vehiclelistAdapter(availableVehicles, registeredvehicles.this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, true);
        manager.setStackFromEnd(true);
        registeredVehiclesList.setAdapter(adapter);
        registeredVehiclesList.setLayoutManager(manager);
        mcontext.getContext().whichFragment=0;
        mcontext.getContext().getFragmentOne(adapter,0);


        //populate the recyclerview
        //get available vehicles in the database
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentUserNode = root.child("registeredVehicles")
                .child(FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid());
        currentUserNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
                availableVehicles.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    availableVehicles.add(sn);
                    adapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


      /*  vehicleType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem()!=null){
                    itemSelected[0] =parent.getSelectedItem().toString();
                }
            }
        });*/

        registerVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                                    Toast.makeText(getActivity(), "vehicle registered successfully", Toast.LENGTH_LONG).show();
                                                } else {
                                                    dialog1.hide();
                                                    dialog.cancel();
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
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemid = item.getItemId();
        if (itemid == R.id.edit) {
           // Toast.makeText(getActivity(), "edit selected", Toast.LENGTH_LONG).show();
            //edit vehicle info
            editVehicleDetails();
            return true;
        }
        if (itemid == R.id.delete) {
            deleteVehicle();
           // Toast.makeText(getActivity(), "delete selected", Toast.LENGTH_LONG).show();
            //delete vehicle info
            return true;
        }
        if (itemid == R.id.showOnMap) {
            showOnMap();
           // Toast.makeText(getActivity(), "show on map selected", Toast.LENGTH_LONG).show();
            //show on map
            return true;
        }
        if (itemid == R.id.geofence) {
            //geofence
            geofence();
           // Toast.makeText(getActivity(), "geofence selected", Toast.LENGTH_LONG).show();
            return true;
        }
        if(itemid==R.id.save){
            saveCurrentLocation();
            return true;
        }
        if (itemid == R.id.getsavedLocations) {
            displaySavedLocation();
          //  Toast.makeText(getActivity(), "saved locations  selected", Toast.LENGTH_LONG).show();
            return true;
        }


        return false;
    }

    private void displaySavedLocation() {
        //get all the saved locations from the database
        savedVehicles=new ArrayList<>();
        String vehicleId=availableVehicles.get(itemPosition).getKey();
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference savedLocation=root.child("registeredVehicles")
                .child("savedLocations");

        DatabaseReference CurrentVehicleRef=savedLocation.child(vehicleId);
        CurrentVehicleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedVehicles.clear();
                for(DataSnapshot x:snapshot.getChildren()){
                    savedVehicles.add(x);
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            if(savedVehicles.size()>0){
                getActivity().getSupportFragmentManager().beginTransaction().add(savedvehiclesfragment.newInstance("xyz","xyz",savedVehicles),"savedvehicles")
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer,savedvehiclesfragment.newInstance("xyz","xyz",savedVehicles))
                        .commit();
            }  else{
                Toast.makeText(getActivity(),"currently you do not have saved locations for this vehicle",Toast.LENGTH_LONG)
                        .show();
            }
            }
        },1000);

    }

    private void saveCurrentLocation() {
        phoneNumber = availableVehicles
                .get(itemPosition)
                .getValue(vehicleDetails.class)
                .getVehiclePhoneNo().trim();
        //String message="SaveCurrentLocation";
        String message="lat:-1.277952\nlng:36.8214016";
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.selectsimcard,null);
        RadioGroup group=v.findViewById(R.id.radiogroup);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setView(v)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedItemId=group.getCheckedRadioButtonId();
                        if(selectedItemId==R.id.sim1){
                            smsReceiver.getPhoneNumber(phoneNumber);
                            sendSMS(phoneNumber,message,0);

                            requestGpsDataDialog.setTitle("requesting current cordinates");
                            requestGpsDataDialog.setMessage("requesting current cordinates ...please wait");
                        }
                        if(selectedItemId==R.id.sim2){
                            smsReceiver.getPhoneNumber(phoneNumber);
                            sendSMS(phoneNumber,message,1);
                            requestGpsDataDialog.setTitle("requesting current cordinates");
                            requestGpsDataDialog.setMessage("requesting current cordinates ...please wait");
                        }

                    }
                }).setTitle("select sim card to use");
        AlertDialog selectsimcard=builder.create();
        selectsimcard.show();
    }

    private void geofence() {
        //geofence from here
        phoneNumber = availableVehicles
                .get(itemPosition)
                .getValue(vehicleDetails.class)
                .getVehiclePhoneNo().trim();
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.geofenceinputlayout,null);
        EditText Distance=v.findViewById(R.id.distance);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle("Geofence service ")
                .setView(v)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String distance=Distance.getText().toString();
                        String commandMsg="geofenceDistance:"+distance;
                        View selectSimCard=LayoutInflater.from(getActivity()).inflate(R.layout.selectsimcard,null);
                        RadioGroup group=selectSimCard.findViewById(R.id.radiogroup);
                        AlertDialog.Builder simCardDialog=new AlertDialog.Builder(getActivity())
                                .setCancelable(false)
                                        .setTitle("select sim card to use")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                      int selectedItem=group.getCheckedRadioButtonId();
                                                      if(selectedItem==R.id.sim1){
                                                          smsReceiver.getPhoneNumber(phoneNumber);
                                                          sendSMS(phoneNumber,commandMsg,0);
                                                          requestGpsDataDialog.setTitle("setting geofence");
                                                          requestGpsDataDialog.setMessage("sending distance to the tracker ...please wait");
                                                      }
                                                      if(selectedItem==R.id.sim2){
                                                          smsReceiver.getPhoneNumber(phoneNumber);
                                                          sendSMS(phoneNumber,commandMsg,1);
                                                          requestGpsDataDialog.setTitle("setting geofence");
                                                          requestGpsDataDialog.setMessage("sending distance to the tracker ...please wait");
                                                      }
                                                    }
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).setView(selectSimCard);
                        AlertDialog simcarddialog=simCardDialog.create();
                        simcarddialog.show();



                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog geofenceDialog=builder.create();
        geofenceDialog.show();
    }

    // @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onResume() {
        super.onResume();
        sentStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = " request sent successfully!!";
                        requestGpsDataDialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestGpsDataDialog.hide();//hide the dialog after one minute
                                Toast.makeText(getActivity(),"took too long to get response " +
                                        "!,please try again ",Toast.LENGTH_LONG).show();

                            }
                        },60000);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";

                        break;
                    default:
                        break;
                }
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }

        };
        deliveredStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "request Not Delivered";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = " request sent sucessfully ..please wait response";
                        break;
                    case Activity.RESULT_CANCELED:
                        s = "request delivery canceled ";
                        break;
                }
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }
        };
        getActivity().registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"), Context.RECEIVER_EXPORTED);
        getActivity().registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"), Context.RECEIVER_EXPORTED);
    }


    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(sentStatusReceiver);
        getActivity().unregisterReceiver(deliveredStatusReceiver);
    }

    private void showOnMap() {
        requestGpsDataDialog.setTitle("Requesting gps data");
        requestGpsDataDialog.setMessage("waiting for gps coordinates...please wait");
        String message = "latitude:-1.277952\nlongitude:36.8214016";//***

        if (checkSelfPermission(getActivity(), SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        //send location request to the gps
        //wait for the response
        //if response obtained ,display the location on the map
        phoneNumber = availableVehicles
                .get(itemPosition)
                .getValue(vehicleDetails.class)
                .getVehiclePhoneNo().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(getActivity(), "phone number not available ", Toast.LENGTH_LONG).show();
        } else {
            final int[] simcard = new int[1];
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.selectsimcard,null);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RadioGroup group=view.findViewById(R.id.radiogroup);
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(R.id.sim1==checkedId){
                        simcard[0] =0;
                    }
                    if(R.id.sim2==checkedId){
                        simcard[0]=1;
                    }
                }
            });
            AlertDialog.Builder selectSimCardDialog=
                    new AlertDialog.Builder(getActivity())
                            .setView(view)

                            .setCancelable(false)
                            .setTitle("select sim card to use")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //continue
                                    int selectedsimcard=group.getCheckedRadioButtonId();
                                    if(selectedsimcard==R.id.sim1){
                                       // Toast.makeText(getActivity(),"selected sim 1",Toast.LENGTH_LONG).show();
                                        smsReceiver.getPhoneNumber(phoneNumber);
                                        sendSMS(phoneNumber,message,0);

                                    }
                                    if(selectedsimcard==R.id.sim2){
                                        //Toast.makeText(getActivity(),"selected sim 2",Toast.LENGTH_LONG).show();
                                        smsReceiver.getPhoneNumber(phoneNumber);
                                        sendSMS(phoneNumber,message,1);
                                    }


                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            });
            AlertDialog selectDialog=selectSimCardDialog.create();
            selectDialog.show();

            Toast.makeText(getActivity(), phoneNumber, Toast.LENGTH_LONG).show();
        }
        // if message length is too long messages are divided        List<String> messages = sms.divideMessage(message);

        /*PendingIntent sentIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_DELIVERED"), PendingIntent.FLAG_IMMUTABLE);
        try {
            sms.sendTextMessage("+254759815336", "0704533980", message, null, null);
            // sms.sendTextMessage(phoneNumber, sc, message, sentIntent, deliveredIntent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "failed to send sms", Toast.LENGTH_LONG).show();
        }*/

    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, "locations", null, null);
                    Toast.makeText(getActivity(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

*/
    public void sendSMS(final String number, final String text,int simcard) {
        final PendingIntent localPendingIntent1 =
                PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE);
        final PendingIntent localPendingIntent2 =
                PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_DELIVERED"), PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= 22) {

            SubscriptionManager subscriptionManager = ((AppCompatActivity) getActivity()).getSystemService(SubscriptionManager.class);
            if (checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                ActivityCompat.requestPermissions(getActivity(), new String[]{READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simcard);
            SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.getSubscriptionId()).sendTextMessage(number, null, text, localPendingIntent1, localPendingIntent2);
        }
        }

    private void deleteVehicle() {
        ProgressDialog deleteDialog=new ProgressDialog(getActivity());
        deleteDialog.setTitle("delete");
        deleteDialog.setMessage("deleting this vehicle...");

        //delete vehicle
        String user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentvehicleRef=root.child("registeredVehicles")
                .child(user).child(Objects.requireNonNull(availableVehicles.get(itemPosition).getKey()));
        deleteDialog.show();
        currentvehicleRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteDialog.hide();
              if(task.isSuccessful()){
                  adapter.notifyDataSetChanged();
                  Toast.makeText(getActivity(),"deleted successfully",Toast.LENGTH_LONG).show();
              } else{
                  Toast.makeText(getActivity(),"failed to delete this vehicle ",Toast.LENGTH_LONG).show();
              }
            }
        });
    }

    private void editVehicleDetails() {
        View view =LayoutInflater.from(getActivity()).inflate(R.layout.addvehicle,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setTitle("Edit Vehicle Details").setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText regNo=view.findViewById(R.id.regNo);
                        EditText phoneNo=view.findViewById(R.id.phoneNo);
                        Spinner vehicleType=view.findViewById(R.id.vehicleType);
                        final String[] itemSelected = {""};
                        itemSelected[0]=vehicleType.getSelectedItem().toString();
                        ProgressDialog dialog1=new ProgressDialog(getActivity());
                        dialog1.setMessage("Editing this vehicle Details ...please wait");
                        dialog1.setTitle("vehicle Details Editing");
                        vehicleDetails vehicledetails=new vehicleDetails();
                        vehicledetails.setVehiclePhoneNo(phoneNo.getText().toString().trim());
                        vehicledetails.setVehicleRegNo(regNo.getText().toString().trim());
                        vehicledetails.setVehicleType(itemSelected[0]);
                        String user= Objects.requireNonNull(FirebaseAuth.getInstance()
                                        .getCurrentUser())
                                .getUid();
                        DatabaseReference root=FirebaseDatabase
                                .getInstance().getReference();
                        dialog1.show();
                        root
                                .child("registeredVehicles").child(user)
                                .child(availableVehicles.get(itemPosition).getKey())
                                .setValue(vehicledetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dialog1.hide();
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity(),"vehicle Details Edited successfully",Toast.LENGTH_LONG).show();
                                        }else{
                                            dialog1.hide();
                                            dialog.cancel();
                                            Toast.makeText(getActivity(),"failed to Edit  vehicle Details ",Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                    }
                }).setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
       //edit from here
    }

    @Override
    public void getItemPosition(int position) {
        this.itemPosition=position;
    }

    @Override
    public void showGoogleMaps(String latitude, String longitude) {
        String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "current vehicle location" + ")";
        Intent Mapintent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        getActivity().startActivity(Mapintent);
    }

    @Override
    public void pushNotification(String details) {

        Intent startServiceIntent=new Intent(getContext(), ShowNotificationService.class);
        startServiceIntent.putExtra("details",details);
        getContext()
                .startService(startServiceIntent);


       /* NotificationManager notificationManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myNotificationChanel";
            String description = "my notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_chanel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager =(NotificationManager) getSystemService(getActivity(),NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "my_chanel")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("geofence broken")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("This is to alert you that your vehicle is out of geofence\n more details"+details))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(1,builder.build());*/

    }


    @Override
    public void saveCurrentLocation(String lat, String lng, String date) {
        //save current location from here
        curentLocationDetails currentloc=new curentLocationDetails();
        currentloc.setLatitude(lat);
        currentloc.setLongitude(lng);
        currentloc.setSaveDate(date);
        ProgressDialog dialog=new ProgressDialog(getActivity());
        dialog.setMessage("adding location to database");
        dialog.setTitle("save location");
        dialog.show();

        String vehicleId=availableVehicles.get(itemPosition).getKey();
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference savedLocation=root.child("registeredVehicles")
                .child("savedLocations");

        DatabaseReference CurrentVehicleRef=savedLocation.child(vehicleId);
       String key= CurrentVehicleRef.push().getKey();

        CurrentVehicleRef.child(key).setValue(currentloc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.hide();
                if(task.isSuccessful()){

                    Toast.makeText(getActivity(),"current vehicles location saved successfully",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(),"failed to save current location",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}