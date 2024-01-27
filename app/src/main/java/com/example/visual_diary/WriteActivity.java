package com.example.visual_diary;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnSelectDate;
    private Button btnSpeechToText;

    private Calendar selectedDate = Calendar.getInstance();
    private DatabaseReference databaseReference;

    private static final int SPEECH_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        editText = findViewById(R.id.editText);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSpeechToText = findViewById(R.id.btnSpeechToText);

        // Set the current date as the default selected date
        selectedDate = Calendar.getInstance();

        // Display the default date in the button
        updateDateButtonText();

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        btnSpeechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });

        Button btnSaveData = findViewById(R.id.btnSaveData);
        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is authenticated
        if (currentUser != null) {
            // Use the user's ID to structure the Realtime Database
            String userId = currentUser.getUid();
            // Assuming "data" is the node where you want to store the text
            // You can customize the database structure based on your requirements
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("data");
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the user is not authenticated
        }
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        btnSelectDate.setText(sdf.format(selectedDate.getTime()));
    }


    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        btnSelectDate.setText(sdf.format(selectedDate.getTime()));
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech-to-text not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);

                // Append spoken text to existing text
                String existingText = editText.getText().toString();
                editText.setText(existingText + " " + spokenText);
            }
        }
    }


    private void saveData() {
        String text = editText.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedDateStr = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(selectedDate.getTime());

        // Check if a date has been selected
        if (selectedDateStr.equals("dd_MM_yyyy")) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the databaseReference to store data under the user's ID
        databaseReference.child(selectedDateStr).setValue(text)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(WriteActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WriteActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}