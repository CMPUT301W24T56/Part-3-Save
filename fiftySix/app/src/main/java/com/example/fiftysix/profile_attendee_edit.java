package com.example.fiftysix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class profile_attendee_edit extends AppCompatActivity {
    private ImageButton profile_button_change,go_back_attendee;
    private EditText mail, phoneno, name, home;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_attendee);

        set_profile();
        profile_button_change = findViewById(R.id.profile_image);



        mail = findViewById(R.id.profile_email);
        phoneno = findViewById(R.id.profile_phone);
        name = findViewById(R.id.profile_name);
        String email,person_name;
        profile_button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(open_camera, 100);
            }
        });



        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mail.getText().toString();
                mail.setText(text);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = name.getText().toString();
                name.setText(text);
            }
        });
        phoneno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = name.getText().toString();
                int number = Integer.parseInt(text);
                name.setText(text);
            }
        });


        //go_back_attendee = findViewById(R.id.go_back_main_attendee);
        go_back_attendee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



    }

    // Gets android ID to be used as organizer ID
    // Got from https://stackoverflow.com/questions/60503568/best-possible-way-to-get-device-id-in-android

    public void set_profile(){
        // setting the profile from the database to the app
        //  we need to get the id first of the user to identify it.
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = (Bitmap) extras.get("data");
                if (photo != null) {
                    profile_button_change.setImageBitmap(photo);
                }
            }
        }
    }
}
