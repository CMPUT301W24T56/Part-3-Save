package com.example.fiftysix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class AdminBrowseImages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_images);

        Button btnProfileImages = findViewById(R.id.btnProfileImages);
        Button btnPosterImages = findViewById(R.id.btnPosterImages);
        ImageButton backButton = findViewById(R.id.backButton);

        btnProfileImages.setOnClickListener(v -> {
            navigateToImageDisplay("ProfileImages");
        });

        btnPosterImages.setOnClickListener(v -> {
            navigateToImageDisplay("PosterImages");
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void navigateToImageDisplay(String imageType) {
        Intent intent = new Intent(AdminBrowseImages.this, ImageDisplayActivity.class);
        intent.putExtra("ImageType", imageType);
        startActivity(intent);
    }
}