package com.example.fiftysix;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class send_notification extends AppCompatActivity {
    private ImageButton cancel,send;
    private EditText message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onCreate: not working ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        message = findViewById(R.id.message);
        send = findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sending_message = message.getText().toString();
                notify_attendee(sending_message);
                finish();
            }
        });

    }
    public void notify_attendee(String message){
//        String token = "Token_for_Receiver";
//        OkHttpClient client  = new OkHttpClient();
//        MediaType media = MediaType.parse("application/json");
    }
}

// organizer event adapter code below // might have to change the parameters  to call this, for example the input for co

