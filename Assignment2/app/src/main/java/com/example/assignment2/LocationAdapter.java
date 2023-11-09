// Facilitates interactions between Recycler View and database.
// Responsible for updating entries and displaying search results.

package com.example.assignment2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Location> listLocations;
    private ArrayList<Location> mArrayList;
    private LocationDatabase mDatabase;

    public LocationAdapter(Context context, ArrayList<Location> listLocations) {
        this.context = context;
        this.listLocations = listLocations;
        this.mArrayList = listLocations;
        mDatabase = new LocationDatabase(context);
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_layout, parent, false);
        return new LocationViewHolder(view);
    }

    // Manages each entry in recycler view.
    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        final Location location = listLocations.get(position);

        holder.location_coordinates.setText(Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()));
        holder.location_address.setText(location.getAddress());

        holder.edit_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTaskDialog(location);
            }
        });

        holder.delete_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.deleteLocation(location.getId());

                ((Activity)context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    // Filters entries based on address.
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    listLocations = mArrayList;
                } else {

                    ArrayList<Location> filteredList = new ArrayList<>();

                    for (Location location : mArrayList) {

                        if (location.getAddress().toLowerCase().contains(charString)) {

                            filteredList.add(location);
                        }
                    }

                    listLocations = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listLocations;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listLocations = (ArrayList<Location>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return listLocations.size();
    }

    // Processes editing a location entry through the entry's add location button.
    private void editTaskDialog(final Location location){
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.add_location_layout, null);

        final EditText lat_field = subView.findViewById(R.id.lat_field);
        final EditText long_field = subView.findViewById(R.id.long_field);

        if(location != null){
            lat_field.setText(Double.toString(location.getLatitude()));
            long_field.setText(Double.toString(location.getLongitude()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit location");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("Update location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    final double lat = Double.parseDouble(lat_field.getText().toString());
                    final double lng = Double.parseDouble(long_field.getText().toString());

                    mDatabase.updateLocation(new Location(location.getId(), lat, lng, geocode(lat,lng)));
                    ((Activity)context).finish();
                    context.startActivity(((Activity)context).getIntent());

                } catch (Exception e) {
                    Toast.makeText(context, "Fields cannot be left empty.", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    public String geocode(double lat, double lng) {
        String address = null;
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> ls = geocoder.getFromLocation(lat, lng, 5);
                for (Address addr : ls) {
                    address = addr.getAddressLine(0);
                }
            } catch (IOException e) {
            }
        }
        if (address != null){
            return address;
        } else {
            return "Address not available.";
        }

    }
}


