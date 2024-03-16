package com.example.fiftysix;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrganizerAttendeeDataActivity extends AppCompatActivity {

    private Organizer organizer;
    private String organizerID;
    private OrganizerAttendeesEventAdapter organizerAttendeesEventAdapter;
    private ArrayList<Event> eventAttendeeDataList;
    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private CollectionReference orgEventRef;
    private CollectionReference eventRef;
    private CollectionReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_attendee_data);
        Context context = getApplicationContext();
        recyclerView = findViewById(R.id.orgAttendeeDataRecyclerView);

        ImageButton buttonBackCreateEvent = (ImageButton) findViewById(R.id.buttonBackCreateEvent);

        // Creates Organizer Object
        organizer = new Organizer(context);
        organizerID = organizer.getOrganizerID();

        eventAttendeeDataList = new ArrayList<>();
        organizerAttendeesEventAdapter = new OrganizerAttendeesEventAdapter(eventAttendeeDataList, this);
        recyclerView.setAdapter(organizerAttendeesEventAdapter);
        recyclerView.setHasFixedSize(false);

        db = FirebaseFirestore.getInstance();
        orgEventRef = db.collection("Users").document(organizer.getOrganizerID()).collection("EventsByOrganizer");
        eventRef = db.collection("Events");
        imageRef = db.collection("Images");

        loadOrganizerAttendees();

        buttonBackCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }




    private void loadOrganizerAttendees(){
        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (value != null){
                    eventRef.whereEqualTo("organizer", organizerID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            if (querySnapshot != null) {
                                eventAttendeeDataList.clear();
                                for (QueryDocumentSnapshot doc : querySnapshot) {
                                    Log.e(TAG, "onEvent: organizer " + doc.getString("organizer").toString() + " organizerID = " + organizerID);
                                    String eventOrganizer = doc.getString("organizer").toString();
                                    Log.e(TAG, "Inside if: organizer " + doc.getString("organizer"));
                                    String eventID = doc.getId();
                                    String eventName = doc.getString("eventName");
                                    String posterID = doc.getString("posterID");
                                    Integer inAttendeeLimit = doc.getLong("attendeeLimit").intValue();
                                    Integer inAttendeeCount = doc.getLong("attendeeCount").intValue();
                                    String inDate = doc.getString("date");
                                    String location = doc.getString("location");
                                    String details = doc.getString("details");

                                    Log.d("EVENTNAME", "hello " + eventID);


                                    if (inAttendeeCount > 0){
                                        eventRef.document(eventID).collection("attendeesAtEvent").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                if (querySnapshot != null) {
                                                    for (QueryDocumentSnapshot attendeeDoc : querySnapshot) {
                                                        String attendeeID = attendeeDoc.getId();
                                                        db.collection("Profiles").document(attendeeID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (documentSnapshot != null) {
                                                                    String attendeeName = documentSnapshot.getString("name");
                                                                    String attendeePhoneNumber = documentSnapshot.getString("phone");
                                                                    String attendeeEmail = documentSnapshot.getString("email");
                                                                    String profileURL = documentSnapshot.getString("profileImageURL");
                                                                    eventAttendeeDataList.add(new Event(eventID, attendeeName, attendeePhoneNumber, attendeeEmail, "temp hard coded for testing", 0, 0, profileURL));
                                                                    organizerAttendeesEventAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }



}