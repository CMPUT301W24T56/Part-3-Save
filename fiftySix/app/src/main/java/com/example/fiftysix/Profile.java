package com.example.fiftysix;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Profile {

    private static final String TAG = "Profile";


    private String userID;
    private String name;
    private String email;
    private String phoneNumber;
    private String bio;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private CollectionReference profileRef;
    private String profileID;
    private String imageID;
    private String currentImageID;
    private String imageUrl;




    public interface ProfileUploadCallback {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(Exception e);
    }

    // TODO: check if profile exists already or if this is a new profile.
    // TODO: Upload data to firebase.
    // TODO: Allow changes to image.

    public Profile(String userID){
        this.userID = userID;
        this.bio = bio;
        this.db = FirebaseFirestore.getInstance();
        this.userRef = db.collection("Users");
        this.profileRef = db.collection("Profiles");
        this.imageUrl = ("https://ui-avatars.com/api/?rounded=true&name=NA&background=random&size=512");
        this.profileID = profileID;

        profileRef.document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {}
                else {
                    addProfileToDatabase();
                }
            }
        });
    }

    public void deleteProfilePic(String userID){
        userRef.document(userID).update("profileImageURL", "https://ui-avatars.com/api/?rounded=true&name="+ this.name +"&background=random&size=512");
    }

    public void editProfile(String userID, String name, String email, String phoneNumber, String bio){
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        userRef.document(userID).update("name", name);
        userRef.document(userID).update("phone", phoneNumber);
        userRef.document(userID).update("email", email);
        userRef.document(userID).update("bio", bio);


        profileRef.document(userID).update("name", name);
        profileRef.document(userID).update("phone", phoneNumber);
        profileRef.document(userID).update("email", email);
        profileRef.document(userID).update("bio", bio);

        if (this.imageUrl == "https://ui-avatars.com/api/?rounded=true&name=NA&background=random&size=512"){
            this.imageUrl = "https://ui-avatars.com/api/?rounded=true&name="+ name +"&background=random&size=512";
            userRef.document(userID).update("profileImageURL", this.imageUrl);
            profileRef.document(userID).update("profileImageURL", this.imageUrl);
        }
    }

    public void setProfileURL(String imageURL){
        this.imageUrl = imageURL;
        userRef.document(userID).update("profileImageURL", this.imageUrl);
        return;
    }


    public String getProfileURL(){
        return this.imageUrl;
    }


    private boolean profileInDataBase(){
        return (FirebaseDatabase.getInstance().getReference("Users/"+userID+"/name") != null );

    }




    private void addProfileToDatabase(){
        Map<String,Object> profileData = new HashMap<>();
        profileData.put("userID","unknown");
        profileData.put("name","unknown");
        profileData.put("email","unknown");
        profileData.put("phone","unknown");
        profileData.put("bio","unknown");
        profileData.put("profileImageURL",this.imageUrl);


        // Adds profile to Profiles collection
        db.collection("Profiles")
                .document(this.userID)
                .set(profileData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Profile Data successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "ERROR: Profile Data failed to upload.");
                    }
                });
    }



    public void uploadImageAndStoreReference(Uri imageUri, ProfileUploadCallback callback) {
        if (imageUri != null) {
            // Gets new key for image ID & sets the key to current image ID.
            this.imageID = FirebaseDatabase.getInstance().getReference("Images").push().getKey();
            this.currentImageID = "images/profile/" + imageID + ".jpg";

            // gets path to where image is stored
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("images/profile/" +imageID + ".jpg");

            // Stores Image in firebase
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        Log.d(TAG, "Image Upload Successful. Image Uri: " + downloadUri.toString());


                        storeImageReferenceInIMAGES(downloadUri.toString());
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
            storeImageReferenceInIMAGES(imageUrl);
            callback.onUploadSuccess(imageUrl);


        }
    }


    // Method to store the image reference in Firestore
    private void storeImageReferenceInIMAGES(String imageUrl) {

        this.imageUrl = imageUrl;
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageURL", imageUrl);
        imageData.put("profileID", profileID);
        imageData.put("userID", userID);
        imageData.put("type", "profilePicture");
        db.collection("ProfileImages").document(imageID).set(imageData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Firestore update successful for profile image user: " + userID);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore update failed for profile image user: " + userID, e);
                });
    }

    //
    public void storeImageInUser(String imageUrlin) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("profileImageURL", imageUrlin);

        db.collection("Users").document(userID).update("profileImageURL", imageUrlin)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Events collection update successful for URL");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Events collection update failure for URL");
                });
    }


}
