//package com.example.visual_diary;
//
//// ViewDataActivity.java
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.Objects;
//
//public class ViewDataActivity extends AppCompatActivity {
//
//    private EditText editText;
//    private Button btnSaveChanges;
//    private String selectedDate;
//    private DatabaseReference databaseReference;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_data);
//
//        editText = findViewById(R.id.editText);
//        btnSaveChanges = findViewById(R.id.btnSaveChanges);
//
//        // Get the selected date from the intent
//        selectedDate = getIntent().getStringExtra("selectedDate");
//
//        // Retrieve and display data for the selected date
//        retrieveData(selectedDate);
//
//        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveChanges();
//            }
//        });
//    }
//
//    private void retrieveData(String selectedDate) {
//        // Get the current user
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Check if the user is authenticated
//        if (currentUser != null) {
//            // Use the user's ID to structure the Realtime Database
//            String userId = currentUser.getUid();
//            // Assuming "data" is the node where you want to retrieve the text
//            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("data");
//
//            // Retrieve data for the selected date
//            databaseReference.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Data exists for the selected date
//                        String data = Objects.requireNonNull(dataSnapshot.getValue()).toString();
//                        editText.setText(data);
//                    } else {
//                        // No data found for the selected date
//                        editText.setText("");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle error
//                }
//            });
//        } else {
//            // Handle the case where the user is not authenticated
//        }
//    }
//
//    private void saveChanges() {
//        String updatedText = editText.getText().toString().trim();
//
//        if (!updatedText.isEmpty()) {
//            // Use the databaseReference to update data for the selected date
//            databaseReference.child(selectedDate).setValue(updatedText);
//            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
