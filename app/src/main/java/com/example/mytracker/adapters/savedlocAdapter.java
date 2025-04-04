package com.example.mytracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytracker.R;
import com.example.mytracker.models.curentLocationDetails;
import com.example.mytracker.savedvehiclesfragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class savedlocAdapter extends RecyclerView.Adapter<savedlocAdapter.ViewHolder> implements Filterable {
    ArrayList<DataSnapshot> savedLoc;
    ArrayList<DataSnapshot> searchedSavedLoc;
    savedvehiclesfragment context;

    public savedlocAdapter(ArrayList<DataSnapshot> savedLoc, savedvehiclesfragment context) {
        this.searchedSavedLoc=savedLoc;
        this.savedLoc = searchedSavedLoc;
        this.context=context;
    }

    @NonNull
    @Override
    public savedlocAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.saveloclayout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull savedlocAdapter.ViewHolder holder, int position) {
     holder.date.setText(savedLoc.get(position).getValue(curentLocationDetails.class).getSaveDate());
     holder.latitude.setText("Latitude : "+savedLoc.get(position).getValue(curentLocationDetails.class).getLatitude());
     holder.longitude.setText("Longitude : "+savedLoc.get(position).getValue(curentLocationDetails.class).getLongitude());
    holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return savedLoc.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<DataSnapshot> searchedResult=new ArrayList<>();
                if(constraint.toString().isEmpty()){
                  searchedResult=searchedSavedLoc;
                }else{
                    for(int x=0;x<searchedSavedLoc.size();x++){
                       if( searchedSavedLoc.get(x).getValue(curentLocationDetails.class).getSaveDate().contains(constraint.toString().trim())){
                           searchedResult.add(searchedSavedLoc.get(x));
                       }
                    }
                }
               FilterResults res=new FilterResults();
                res.values=searchedResult;
                return res;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
        savedLoc=(ArrayList<DataSnapshot>) results.values;
        notifyDataSetChanged();
        context.updateAdapter();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView latitude;
        TextView longitude;
        TextView showonmap;
        ImageView delete;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            date = itemView.findViewById(R.id.savedate);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longitude);
            showonmap = itemView.findViewById(R.id.showonmaps);
            delete = itemView.findViewById(R.id.delete);}

            public void bind( int position){
                showonmap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String lat = savedLoc.get(position).getValue(curentLocationDetails.class).getLatitude();
                        String lng = savedLoc.get(position).getValue(curentLocationDetails.class).getLongitude();
                        context.showGoogleMaps(lat, lng);
                    }
                });
            }

        }
    }
