package com.example.assignment2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private LocationDatabase mDatabase;
    private ArrayList<Location> allLocations = new ArrayList<>();
    private LocationAdapter mAdapter;

    // Prepares database and views, and manages the import and add location buttons.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FrameLayout fLayout = (FrameLayout) findViewById(R.id.activity_to_do);

        SearchView searchView = (SearchView)findViewById(R.id.search_bar);
        search(searchView);

        RecyclerView locationView = (RecyclerView)findViewById(R.id.location_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        locationView.setLayoutManager(linearLayoutManager);
        locationView.setHasFixedSize(true);
        mDatabase = new LocationDatabase(this);
        allLocations = mDatabase.listLocations();

        if(allLocations.size() > 0){
            locationView.setVisibility(View.VISIBLE);
            mAdapter = new LocationAdapter(this, allLocations);
            locationView.setAdapter(mAdapter);

        }else {
            locationView.setVisibility(View.GONE);
            Toast.makeText(this, "No locations found. Add locations, or import from file.", Toast.LENGTH_LONG).show();
        }
        FloatingActionButton addLocationButton = (FloatingActionButton) findViewById(R.id.add_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });
        FloatingActionButton importButton = (FloatingActionButton) findViewById(R.id.import_button);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importLocations();
            }
        });

    }

    // Processes adding a location entry through the add location button.
    private void addTaskDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_location_layout, null);

        final EditText latField = subView.findViewById(R.id.lat_field);
        final EditText longField = subView.findViewById(R.id.long_field);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new location");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("Add Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    final double lat = Double.parseDouble(latField.getText().toString());
                    final double lng = Double.parseDouble(longField.getText().toString());

                    Location newLocation = new Location(lat, lng, geocode(lat,lng));
                    mDatabase.addLocation(newLocation);
                    finish();
                    startActivity(getIntent());

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Fields cannot be left empty.", Toast.LENGTH_LONG).show();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }
    // Triggers search filter in the recycler view if a query is present.
    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter!=null)
                    mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
    // Deletes all existing entries and populates the database with entries from the input file.
    // Input file must be present at /storage/emulated/0/sampledataset.csv in the emulated device.
    // Prompts user for permission if app does not have permission to access files.
    public void importLocations() {
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
        try {
            mDatabase.deleteAllLocations();
            File csvfile = new File(Environment.getExternalStorageDirectory() + "/sampledataset.csv");
            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                double lat = Double.parseDouble(nextLine[0]);
                double lng = Double.parseDouble(nextLine[1]);

                Location newLocation = new Location(lat, lng, geocode(lat,lng));
                mDatabase.addLocation(newLocation);
                finish();
                startActivity(getIntent());
            }
        } catch (Exception e) {
            Log.e("ExceptionTag", e.getMessage(), e);
            Toast.makeText(this, "Data set file not found." + Environment.getExternalStorageDirectory(), Toast.LENGTH_SHORT).show();
        }
    }
    // Takes latitude/longitude and returns address using Geocode.
    public String geocode(double lat, double lng) {
        String address = null;
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> ls = geocoder.getFromLocation(lat, lng, 1);
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
