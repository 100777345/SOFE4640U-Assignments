// Manages components in a location entry in the recycler view.

package com.example.assignment2;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    public TextView location_coordinates, location_address;
    public ImageView delete_location;
    public  ImageView edit_location;

    public LocationViewHolder(View itemView) {
        super(itemView);
        location_coordinates = (TextView)itemView.findViewById(R.id.location_coordinates);
        location_address = (TextView)itemView.findViewById(R.id.location_address);
        delete_location = (ImageView)itemView.findViewById(R.id.delete_location);
        edit_location = (ImageView)itemView.findViewById(R.id.edit_location);
    }
}