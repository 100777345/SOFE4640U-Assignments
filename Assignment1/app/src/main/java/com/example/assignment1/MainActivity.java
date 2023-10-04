// Nagahiro Aoyama, 100777345, SOFE4640U
package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.assignment1.databinding.ActivityMainBinding;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Returns false if an empty field exists, otherwise returns true
    public boolean fillFieldsCheck() {
        if (binding.mortgageInput.getText().toString().isEmpty() ||
                binding.interestInput.getText().toString().isEmpty() ||
                binding.amortYearsInput.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public void calculate(){

        // Check if all fields are filled
        if (fillFieldsCheck() == true){

            // Prepare output format
            DecimalFormat dec = new DecimalFormat("#.00");

            // Read and prepare input values
            double mortgage = Double.parseDouble(binding.mortgageInput.getText().toString());
            double effective_interest_rate = Double.parseDouble(binding.interestInput.getText().toString())/1200.00;
            int total_payments = Integer.parseInt(binding.amortYearsInput.getText().toString()) * 12;

            // Calculate EMI
            double monthlyPayResult = mortgage * effective_interest_rate * Math.pow(1+effective_interest_rate,total_payments) / (Math.pow(1+effective_interest_rate,total_payments)-1);

            // Display formatted EMI
            binding.monthlyPayOutput.setText(dec.format(monthlyPayResult));

            // Clear error message, if any
            binding.errorMessage.setText("");

        } else {
            // Display error message
            binding.errorMessage.setText("Please fill in all fields.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiate calculateButton, button calls calculate() when pressed
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
            }
        });
    }
}