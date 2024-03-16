package com.example.fiftysix;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Poster {
    private static final String TAG = "Poster";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/fiftysix-a4bcf.appspot.com/o/images%2FPosters%2Fno-photos.png?alt=media";


    public interface PosterUploadCallback {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(Exception e);
    }

    // Method to upload the image to Firebase Storage and store its reference in Firestore
    public void uploadImageAndStoreReference(Uri imageUri, String posterID, String eventType, PosterUploadCallback callback) {
        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("images/Posters/" + posterID + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        Log.d(TAG, "Image Upload Successful. Image Uri: " + downloadUri.toString());
                        storeImageReferenceInIMAGES(downloadUri.toString(), posterID, eventType);
                        callback.onUploadSuccess(downloadUri.toString());
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "URL retrieval failed.", e);
                        callback.onUploadFailure(e);
                    }))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Image Upload Failed.", e);
                        callback.onUploadFailure(e);
                    });
        } else {
            Log.d(TAG, "No Image Selected! Using default poster.");
            storeImageReferenceInIMAGES(DEFAULT_IMAGE_URL, posterID, eventType);
            callback.onUploadSuccess(DEFAULT_IMAGE_URL);
        }
    }

    // Method to store the image reference in Firestore
    private void storeImageReferenceInIMAGES(String imageUrl, String posterName, String eventType) {
        Map<String, Object> posterData = new HashMap<>();
        posterData.put("image", imageUrl);
        posterData.put("poster", posterName);
        posterData.put("type", eventType);

        db.collection("PosterImages").document(posterName).set(posterData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Firestore update successful for poster: " + posterName);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore update failed for poster: " + posterName, e);
                });
    }


}
