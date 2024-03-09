package com.example.fiftysix;

// Has a profile they can view\edit.
// Can View event details and announcements within the app event.
// Check into an event by scanning the provided QR code.
// Recieve push notifications from event organizers.
// Can log in without username or password (Use device ID). GET DEVICE ID



// Can enable/disable geolocation tracking. (NOT FOR PART 3)



import static android.hardware.usb.UsbDevice.getDeviceId;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;


public class Attendee {

    private Context mContext;
    private String attendeeID;
    private FirebaseFirestore db;
    private CollectionReference ref;
    private String userType = "attendee";
    private String eventID;

    private Profile profile; // TODO: create and store attendee profile


    // ________________________________CONSTRUCTORS_____________________________________


    public Attendee(Context mContext) {
        this.mContext = mContext;
        this.attendeeID = getDeviceId();
        this.db = FirebaseFirestore.getInstance();
        this.ref = db.collection("Users");

        // Adds organizer to data base if the organizer doesn't already exist
        attendeeExists();
    }




    // ________________________________METHODS_____________________________________

    // Adds check in data to the database
    public void checkInToEvent(String qRCodeID){

        Map<String,Object> attendeeCheckedInEventsData = new HashMap<>();
        attendeeCheckedInEventsData.put("eventDate","tempDate");
        //this.ref.document(this.attendeeID).collection("CheckedIntoEvents").document(eventID).set(attendeeCheckedInEventsData);

        Map<String,Object> attendeeCheckedInCount = new HashMap<>();
        attendeeCheckedInCount.put("timesCheckedIn",0);

        //https://firebase.google.com/docs/firestore/query-data/get-data#java_4
        // Use this to fetch specific document.
        DocumentReference docRef = db.collection("CheckInQRCode").document(qRCodeID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        eventID = document.get("event").toString();
                        ref.document(attendeeID).collection("UpcomingEvents").document(eventID).set(attendeeCheckedInEventsData);

                        // TODO: increment this data every time the same attendee checks into the same event.
                        db.collection("Events").document(eventID).collection("attendeesAtEvent").document(attendeeID).set(attendeeCheckedInCount);

                        db.collection("Events").document(eventID).update("attendeeCount",FieldValue.increment(1));
                        Log.d(TAG, "DocumentSnapshot data: " + eventID);
                    } else {
                        Log.d(TAG, "No such document");

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    // Adds event to users upcoming events colletion in firebase.
    private void addUpcomingEventToAttendeeDataBase(String eventIDKey){
        Map<String,Object> attendeeUpcomingEventsData = new HashMap<>();
        attendeeUpcomingEventsData.put("eventDate","temp");
        this.ref.document(this.attendeeID).collection("UpcomingEvents").document(eventIDKey).set(attendeeUpcomingEventsData);
    }


    // Gets android ID to be used as attendee ID.
    // Got from https://stackoverflow.com/questions/60503568/best-possible-way-to-get-device-id-in-android
    public String getDeviceId() {
        String id = Settings.Secure.getString(this.mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return id;
    }

    // Adds attendee to database if they are not already in it.
    private void addAttendeeToDatabase(){
        Map<String,Object> attendeeData = new HashMap<>();
        attendeeData.put("type",this.userType);
        attendeeData.put("name","unknown");
        attendeeData.put("phone","unknown");
        attendeeData.put("email","unknown");
        attendeeData.put("bio","unknown");
        attendeeData.put("profileImageURL","unknown");

        this.ref
                .document(this.attendeeID)
                .set(attendeeData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Attendee Data successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "ERROR: Attendee Data failed to upload.");
                    }
                });
    }

    // Checks if the organizer is already in the database, If not in the database the organizer is added to it.
    // WILL NEED TO REWRITE
    // https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection
    private void attendeeExists(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(this.attendeeID);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Attendee already exists!");
                        //organizerID = "thisOrganizerAlreadyExists";
                        //addOrganizerToDatabase();

                    } else {
                        Log.d(TAG, "Attendee does not already exist!");
                        addAttendeeToDatabase();
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }

            }
        });
    }




    public String getUserType() {
        return userType;
    }
}
