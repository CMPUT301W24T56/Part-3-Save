package com.example.fiftysix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AdminMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_options);

        // Initialize the buttons
        Button eventsButton = findViewById(R.id.browseEvents);
        Button profilesButton = findViewById(R.id.browseProfiles);
        Button imagesButton = findViewById(R.id.browseImages);

        // Set onClickListeners for each button
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AdminBrowseEvents.class);
                startActivity(intent);
            }
        });

        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AdminMainActivity.this, AdminBrowseProfiles.class);
//                startActivity(intent);
            }
        });

        imagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AdminMainActivity.this, AdminBrowseImages.class);
//                startActivity(intent);
            }
        });
    }
}