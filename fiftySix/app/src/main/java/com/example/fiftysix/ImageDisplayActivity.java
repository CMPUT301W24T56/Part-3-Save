package com.example.fiftysix;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ImageDisplayActivity extends AppCompatActivity {

    private RecyclerView imagesRecyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Change the number of columns if needed
        imageAdapter = new ImageAdapter(imageUrls);
        imagesRecyclerView.setAdapter(imageAdapter);
        ImageButton backButton = findViewById(R.id.backButton);

        // Get the image type passed from the AdminBrowseImages activity
        String imageType = getIntent().getStringExtra("ImageType");
        if (imageType != null) {
            fetchImages(imageType);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void fetchImages(String imageType) {

        String collectionPath = imageType.equals("ProfileImages") ? "ProfileImages" : "PosterImages";
        String imageUrlField = imageType.equals("ProfileImages") ? "imageURL" : "image";
        String defaultURL = "https://firebasestorage.googleapis.com/v0/b/fiftysix-a4bcf.appspot.com/o/images%2FPosters%2Fno-photos.png?alt=media";

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageUrls.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString(imageUrlField);
                            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals(defaultURL)) {
                                imageUrls.add(imageUrl);
                            }
                        }
                        imageAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("ImageDisplayActivity", "Error getting images: ", task.getException());
                    }
                });
    }

}