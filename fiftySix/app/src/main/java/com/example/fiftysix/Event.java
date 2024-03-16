package com.example.fiftysix;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String eventID;
    private String organizerID;
    private String posterURL;
    private String posterID;
    private Attendee[] attendeeList;
    private Attendee[] rsvpAttendeeList;
    private String checkInQRCodeID; // Check if should be auto generated upon creating event
    private String details;
    private String location;
    private Integer attendeeLimit;
    private Integer attendeeCount;
    private String eventName;
    private String promoQRCodeID;
    private String queryReturnString;
    private String date;
    private Boolean expandable;

    private CheckInQRCode checkInQRCode;
    private FirebaseFirestore db;
    private CollectionReference ref;
    private Context mContext;
    //private Map map; used after part 3


    // ________________________________CONSTRUCTORS_____________________________________


    // This constructor is used when the organizer does NOT want to reuse a check-in QR code.
    public Event(String organizerID, String details, String location, Integer attendeeLimit, String eventName, String date, Context mContext) {
        this.organizerID = organizerID;

        this.details = details;
        this.location = location;
        this.attendeeLimit = attendeeLimit;
        this.eventName = eventName;
        this.mContext = mContext;
        this.db = FirebaseFirestore.getInstance();
        this.ref = db.collection("Events");
        this.eventID = FirebaseDatabase.getInstance().getReference("Events").push().getKey();
        this.date = date;
        this.posterID = FirebaseDatabase.getInstance().getReference("images").push().getKey();

        // Generates New check-in QR code and stores the qrcode ID.
        this.checkInQRCode = new CheckInQRCode(eventID, mContext);
        this.checkInQRCodeID = this.checkInQRCode.getQRCodeID();
        addEventToDataBase();
    }





    // Used to create an event object from given event ID
    public Event(String eventID, String eventName, String eventLocation, String eventDate, String details, Integer attendeeCount, Integer attendeeLimit,  String posterURL) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.location = eventLocation;
        this.date = eventDate;
        this.details = details;
        this.attendeeCount = attendeeCount;
        this.attendeeLimit = attendeeLimit;
        this.posterURL = posterURL;
        this.expandable = false;
        //this.posterID = FirebaseDatabase.getInstance().getReference("images").push().getKey();
    }





    // This constructor is used when the organizer WANTS to reuse a check-in QR code.
    public Event(String organizerID, String details, String location, Integer attendeeLimit, String eventName, Context mContext, String checkInQRCodeID) {
        this.eventID = FirebaseDatabase.getInstance().getReference("Events").push().getKey();
        this.organizerID = organizerID;
        this.details = details;
        this.location = location;
        this.attendeeLimit = attendeeLimit;
        this.eventName = eventName;
        this.mContext = mContext;
        this.checkInQRCodeID = checkInQRCodeID;
        this.db = FirebaseFirestore.getInstance();
        this.posterID = FirebaseDatabase.getInstance().getReference("images").push().getKey();

        // TODO: check the the event that we are reusing the qrcode from is still active.

        // Switches data
        FirebaseFirestore.getInstance().collection("Events").document(this.eventID).update("checkInQRCode", checkInQRCodeID);
        FirebaseFirestore.getInstance().collection("CheckInQRCode").document(this.checkInQRCodeID).update("event", this.eventID);

    }


    // ________________________________METHODS_____________________________________

    public String getPosterID(){ return posterID;}

    public String getEventID() {
        return eventID;
    }

    // Adds event data to database in firestore.
    private void addEventToDataBase(){



        Map<String,Object> eventData = new HashMap<>();
        eventData.put("attendeeCount",0);
        eventData.put("attendeeLimit",this.attendeeLimit);
        eventData.put("attendeeLimit",this.attendeeLimit);
        eventData.put("attendeeList",this.attendeeList);
        eventData.put("attendeeList",this.rsvpAttendeeList);
        eventData.put("details",this.details);
        eventData.put("eventName",this.eventName);
        eventData.put("organizer",this.organizerID);
        eventData.put("checkInQRCode",this.checkInQRCodeID);
        eventData.put("date",this.date);
        eventData.put("location",this.location);
        eventData.put("attendee",this.attendeeCount);
        eventData.put("posterID",this.posterID);


        // These are optional so we need to check if they are null

        //eventData.put("poster",this.posterID);
        //eventData.put("promoQRCode",this.promoQRCodeID);
        //eventData.put("map",this.map); for after part 3


        this.ref
                .document(eventID)
                .set(eventData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Event Data successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "ERROR: Event Data failed to upload.");
                    }
                });
    }






    //_______________________________Getters________________________________________________________


    public String getOrganizerID() {
        return organizerID;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getCheckInQRCodeID() {
        return checkInQRCodeID;
    }

    public String getDetails() {
        return details;
    }

    public String getLocation() {
        return location;
    }

    public Integer getAttendeeLimit() {
        return attendeeLimit;
    }

    public Integer getAttendeeCount() {
        return attendeeCount;
    }

    public String getEventName() {
        return eventName;
    }

    public String getPromoQRCodeID() {
        return promoQRCodeID;
    }

    public String getDate(){ return date;}

    public Boolean getExpandable() {
        return expandable;
    }

    public void setExpandable(Boolean expandable) {
        this.expandable = expandable;
    }
}
