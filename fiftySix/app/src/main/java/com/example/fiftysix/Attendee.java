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

    // ________________________________CONSTRUCTORS_____________________________________

    /**
     *  Creates attendee object, if the device ID is not currently in the database linked to an attendee it will be added.
     * @param mContext: Context current app context
     */
    public Attendee(Context mContext) {
        this.mContext = mContext;
        this.attendeeID = getDeviceId();
        this.db = FirebaseFirestore.getInstance();
        this.ref = db.collection("Users");
        attendeeExists(); // Adds organizer to data base if the organizer doesn't already exist
    }




    // ________________________________METHODS_____________________________________

    /**
     * Allows the attendee to check into an event. Takes in the QRCode ID, with that ID it fetches what event that qrcode is currently linked to in the database.
     * It then adds the event ID to a collection inside the user document to keep track of events the user has check into.
     * It also increments the attendee count in the event document in the database and stores the attendees id in a sub-collection inside the event.
     * These are used so the organizer can view attendees and track realtime attendance.
     * @param qRCodeID: String of the QRcode ID that was scanned.
     */
    public void checkInToEvent(String qRCodeID){

        Map<String,Object> attendeeCheckedInEventsData = new HashMap<>();
        attendeeCheckedInEventsData.put("eventDate","tempDate");
        //this.ref.document(this.attendeeID).collection("CheckedIntoEvents").document(eventID).set(attendeeCheckedInEventsData);

        Map<String,Object> attendeeCheckedInCount = new HashMap<>();
        attendeeCheckedInCount.put("timesCheckedIn",1);

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


    public void checkInToEventID(String eventID){
        Map<String,Object> attendeeCheckedInEventsData = new HashMap<>();
        attendeeCheckedInEventsData.put("eventDate","tempDate");
        Map<String,Object> attendeeCheckedInCount = new HashMap<>();
        attendeeCheckedInCount.put("timesCheckedIn",1);

        ref.document(attendeeID).collection("UpcomingEvents").document(eventID).set(attendeeCheckedInEventsData);
        db.collection("Events").document(eventID).collection("attendeesAtEvent").document(attendeeID).set(attendeeCheckedInCount);

        db.collection("Events").document(eventID).update("attendeeCount",FieldValue.increment(1));
    }


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
        Map<String,Object> attendeeUpcomingEventsData = new HashMap<>();
        attendeeUpcomingEventsData.put("eventDate","temp");

        Map<String,Object> attendeeData = new HashMap<>();
        attendeeData.put("type",this.userType);
        attendeeData.put("name","unknown");
        attendeeData.put("phone","unknown");
        attendeeData.put("email","unknown");
        attendeeData.put("bio","unknown");
        attendeeData.put("profileImageURL","unknown");
        attendeeData.put("profileID", "unknown");

        new Profile(attendeeID);

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

        //this.ref.document(this.attendeeID).collection("UpcomingEvents").document("temp").set(attendeeUpcomingEventsData);
    }

    // Checks if the organizer is already in the database, If not in the database the organizer is added to it.
    // WILL NEED TO REWRITE
    // https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection
    private void attendeeExists(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("Users").document(attendeeID);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        rootRef.collection("Users").document(attendeeID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot != null){
                                    //profileID = documentSnapshot.getString("profileID");
                                }
                            }
                        });
                        Log.d(TAG, "Attendee already exists!");

                    } else {
                        Log.d(TAG, "Attendee does not already exist!");
                        //profileID = FirebaseDatabase.getInstance().getReference("Profiles").push().getKey();
                        addAttendeeToDatabase();
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }

            }
        });
    }





}
