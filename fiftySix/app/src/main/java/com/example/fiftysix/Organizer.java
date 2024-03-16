package com.example.fiftysix;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodSession;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class Organizer {
    private String organizerID;
    private Context mContext;
    private String userType = "organizer";
    private FirebaseFirestore db;
    private CollectionReference ref;


    public Organizer(Context mContext) {
        this.mContext = mContext;
        this.organizerID = getDeviceId();
        this.db = FirebaseFirestore.getInstance();
        this.ref = db.collection("Users");
        organizerExists(); // Adds organizer to data base if the organizer doesn't already exist
    }


    // ________________________________METHODS_____________________________________

    /**
     * Gets the android device ID uses it as a key in data base and as the organizers ID
     * @return String that is the android device ID
     */
    private String getDeviceId() {
        String id = Settings.Secure.getString(this.mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return id;
    }

    /**
     * Returns the organizer ID/ android device ID
     * @return String organizerID
     */
    public String getOrganizerID(){
        return organizerID;
    }

    /**
     * Creates new Event object and add the event ID to a collection inside the organizers docuement in firebase.
     * @param details String of the event details
     * @param location String of the event location
     * @param attendeeLimit Integer of the attendee limit
     * @param eventName String of the event name
     * @param date String date of the event
     * @return String posterID key
     */
    public String createEventNewQRCode( String details, String location, Integer attendeeLimit, String eventName, String date){
        Event event = new Event(this.organizerID, details, location, attendeeLimit, eventName, date, mContext);
        addEventToOrganizerDataBase(event.getEventID());
        return event.getPosterID();
    }

    /**
     * Creates a new event and switches the event that the oldQRID points to, it now points to the new event in the firebase database.
     * The event is then added to EventsByOrganizer collection inside the organizers document in firebase.
     * @param details String of the event details
     * @param location String of the event location
     * @param attendeeLimit Integer of the attendee limit
     * @param eventName String of the event name
     * @param oldQRID String of the old QR code id to be reused
     */
    public void createEventReuseQRCode(String details, String location, Integer attendeeLimit, String eventName, String oldQRID){
        Event event = new Event(this.organizerID, details, location, attendeeLimit, eventName, mContext, oldQRID);
        addEventToOrganizerDataBase(event.getEventID());
    }


    /**
     * Adds the organizer to the firebase, using the device ID as it's primary key. Stored inside of the Users collection.
     */
    private void addOrganizerToDatabase(){
        Map<String,Object> orgData = new HashMap<>();
        orgData.put("type",this.userType);
        this.ref
                .document(this.organizerID)
                .set(orgData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Organizer Data successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "ERROR: Organizer Data failed to upload.");
                    }
                });
    }


    // Adds event data to database in firestore, this is nested inside the organizer.

    /**
     * Adds the event to a collection "Organizer Events", the is nested in the users docuemnt in fire store. The document ID is an eventID hosted by the organizer.
     * @param eventIDKey
     */
    private void addEventToOrganizerDataBase(String eventIDKey){
        Map<String,Object> orgEventsData = new HashMap<>();
        orgEventsData.put("event","event"); // Only want the document id as it is the event ID, doesn't matter what is in the document
        this.ref.document(this.organizerID).collection("EventsByOrganizer").document(eventIDKey).set(orgEventsData);
    }

    /**
     * Checks if the organizer is already in the firebase, If not in the database the organizer is added to firebase.
     * Reference: https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection
     */
    private void organizerExists(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(this.organizerID);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Organizer already exists!");
                    } else {
                        Log.d(TAG, "Organizer does not already exist!");
                        addOrganizerToDatabase();
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }

            }
        });
    }


}
