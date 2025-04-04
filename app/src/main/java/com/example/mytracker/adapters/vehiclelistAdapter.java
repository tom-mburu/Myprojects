package com.example.mytracker.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytracker.R;
import com.example.mytracker.models.curentLocationDetails;
import com.example.mytracker.models.vehicleDetails;
import com.example.mytracker.registeredvehicles;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class vehiclelistAdapter extends RecyclerView.Adapter<vehiclelistAdapter.ViewHolder> implements Filterable {
    private ArrayList<DataSnapshot> availableVehicles;
    private ArrayList<DataSnapshot> searchedAvailableVehicles;
    private registeredvehicles context;

    public vehiclelistAdapter(ArrayList<DataSnapshot> availableVehicles,registeredvehicles context) {
        this.searchedAvailableVehicles=availableVehicles;

        this.availableVehicles = searchedAvailableVehicles;
        this.context=context;
    }

    @NonNull
    @Override
    public vehiclelistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.registeredvehicleitem,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull vehiclelistAdapter.ViewHolder holder, int position) {
holder.vehicleType.setText(availableVehicles.get(position).getValue(vehicleDetails.class).getVehicleType());
if(availableVehicles.get(position).getValue(vehicleDetails.class).getVehicleType().contains("bike")){
holder.vehicleTypeImage.setImageResource(R.drawable.bike);
}else{
    holder.vehicleTypeImage.setImageResource(R.drawable.bus);
}
holder.vehicleRegNo.setText(availableVehicles.get(position).getValue(vehicleDetails.class).getVehicleRegNo().toUpperCase());
holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return availableVehicles.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<DataSnapshot> searchResult=new ArrayList<>();
                if(constraint.toString().isEmpty()){
                    searchResult=searchedAvailableVehicles;
                }else{
                    for(int x=0;x<searchedAvailableVehicles.size();x++){
                        if(searchedAvailableVehicles.get(x).getValue(vehicleDetails.class).getVehicleRegNo().contains(constraint)){
                         searchResult.add(searchedAvailableVehicles.get(x));
                        }

                    }
                }
                FilterResults res=new FilterResults();
                res.values=searchResult;
                return res;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
         availableVehicles=(ArrayList<DataSnapshot>) results.values;
          notifyDataSetChanged();


            }
        };
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
 public ImageView vehicleTypeImage;
 public TextView vehicleRegNo;
public  TextView vehicleType;
 public LinearLayout menu;
 View itemview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemview=itemView;
            vehicleTypeImage=itemView.findViewById(R.id.vehicletypeimage);
            vehicleRegNo=itemView.findViewById(R.id.vehicleRegNo);
            vehicleType=itemView.findViewById(R.id.vehicleType);
            menu=itemView.findViewById(R.id.menu);
        }
        public void bind(int position){
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getItemPosition(position);
                    PopupMenu popupmenu=new PopupMenu(itemview.getContext(),v);
                    popupmenu.inflate(R.menu.vehiclespecificmenu);
                    popupmenu.setOnMenuItemClickListener(context);
                    popupmenu.show();
                }
            });
        }
    }
}
