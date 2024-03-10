package com.example.fiftysix;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.ContentValues.TAG;
import android.widget.ImageView;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.Manifest;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OrganizerMainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference orgEventRef;
    private CollectionReference eventRef;
    private CollectionReference imageRef;
    private RecyclerView recyclerView;


    // Views
    private ViewFlipper viewFlipper;
    private Organizer organizer;
    private String reUseQRID;

    private int attendeeLimit = Integer.MAX_VALUE;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri cameraImageUri = null; // To store the camera image URI
    private Poster posterHandler;
    private Uri selectedImageUri = null;
    private static final int REQUEST_CAMERA_PERMISSION = 201;


    // Buttons on home pages
    private ImageButton addEventButton;
    private ImageButton orgProfileButton;
    private ImageButton orgNotificationButton;
    private ImageButton orgHomeButton;

    // Buttons on Create event page
    private Button createEvent;
    private ImageButton eventDetailsBack;
    private Button reuseCheckInQR;
    private EditText eventTitleEditText;
    private EditText eventDateEditText;
    private EditText eventAddressEditText;
    private EditText eventDetailsEditText;
    private Switch switchAttendeeLimit;
    private ImageView eventPosterImage;
    // private Button buttonUploadPoster;
    // Buttons on Upload QR page
    private Button uploadQRFromScan;
    private ArrayList<Event> eventDataList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.organizer_flipper);
        viewFlipper = findViewById(R.id.organizerFlipper);
        Context context = getApplicationContext();
        recyclerView = findViewById(R.id.orgHomeRecyclerView);

        // Creates Organizer Object
        organizer = new Organizer(context);

        // Firebase
        db = FirebaseFirestore.getInstance();
        orgEventRef = db.collection("Users").document(organizer.getOrganizerID()).collection("EventsByOrganizer");
        eventRef = db.collection("Events");
        imageRef = db.collection("Images");


        setButtons();
        setEditText();
        setupAttendeeLimitSwitch();


        eventDataList = new ArrayList<>();
        //eventDataList.add(new Event("Event Name", "Event Location", "Event Date"));

        // Sets home page recyler view event data
        OrganizerEventAdapter organizerEventAdapter = new OrganizerEventAdapter(eventDataList);
        recyclerView.setAdapter(organizerEventAdapter);
        recyclerView.setHasFixedSize(false);

        // Creates Poster Object
        posterHandler = new Poster();





        // Adds events from database to the organizers home screen. Will only show events created by the organizer



        orgEventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {

                    Log.e(TAG, "onEvent: 1" );

                    eventDataList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshots) {


                        String eventID = doc.getId();
                        Log.d("EVENTNAME", "hello "+ eventID);




                        eventRef.document(eventID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                Log.e(TAG, "onEvent: 2" );

                                if (error != null) {
                                    Log.e("Firestore", error.toString());
                                    return;
                                }
                                if (value != null && value.exists()) {
                                    String eventName = value.getString("eventName");
                                    String posterID = value.getString("posterID");
                                    Integer inAttendeeLimit = value.getLong("attendeeLimit").intValue();
                                    Integer inAttendeeCount = value.getLong("attendeeCount").intValue();
                                    String inDate = value.getString("date");
                                    String location = value.getString("location");
                                    String details = value.getString("details");
                                    // Gets poster image from database
                                    db.collection("Images").document(posterID).addSnapshotListener(new EventListener<DocumentSnapshot>() {

                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (value != null) {
                                                Log.e(TAG, "onEvent: 3");
                                                String imageUrl = value.getString("image");
                                                eventDataList.add(new Event(eventName, location, inDate, details, inAttendeeCount, inAttendeeLimit, imageUrl));
                                                organizerEventAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });

                                }
                            }
                        });

                    }
                }
            }
        });


        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result && cameraImageUri != null) {
                        selectedImageUri = cameraImageUri;
                        // Now the selectedImageUri contains the URI of the captured image
                    }
                }
        );


        // Opens page to create event
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextView(v);
            }
        });



        // Create event pages
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String eventTitle = eventTitleEditText.getText().toString();
                String eventDate = eventDateEditText.getText().toString();
                String eventAddress = eventAddressEditText.getText().toString();
                String eventDetails = eventDetailsEditText.getText().toString();


                String posterID = organizer.createEventNewQRCode( eventDetails, eventAddress, attendeeLimit, eventTitle, eventDate);




                posterHandler.uploadImageAndStoreReference(selectedImageUri, posterID, "Event", new Poster.PosterUploadCallback() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {

                       // posterHandler.storeImageinEVENT(imageUrl, eventID);


                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        Log.e(TAG, "Failed to upload image for event: " + posterID, e);
                        // Handle failure, e.g., show a toast or alert dialog
                    }
                });





                previousView(v);
            }
        });






        // Opens viewe to reuse android a qrcode for attendee check in.
        reuseCheckInQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextView(v);
            }
        });

        // Switchs layout to previous when user presses back in event details page
        eventDetailsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousView(v);
            }
        });

        // TODO: fix QR reuse method createEventReuseQRCode
        uploadQRFromScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();

                previousView(v);
            }
        });

        Button buttonUploadPoster = findViewById(R.id.buttonUploadPoster);
        buttonUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });



    }



    //________________________________________Methods________________________________________




    private void showImageSourceDialog() {
        CharSequence[] items = {"Upload from Gallery", "Upload from Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Poster");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Upload from Gallery
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(intent);
                        break;
                    case 1: // Upload from Camera
                        Log.d(TAG, "Attempting to launch camera.");
                        if (ContextCompat.checkSelfPermission(OrganizerMainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "RequestCameraPermissionCalled");
                            requestCameraPermission();
                        } else {
                            Log.d(TAG, "Permission is already granted");
                            openCamera();
                        }
                        break;
                }
            }
        });
        builder.show();
    }






    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(OrganizerMainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, open camera
                openCamera();
            } else {
                // Permission was denied
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while creating the file", ex);
                return;
            }
            Uri photoURI = FileProvider.getUriForFile(OrganizerMainActivity.this, "com.example.fiftysix.fileProvider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(photoURI);
        } else {
            Log.d(TAG, "No app can handle the camera intent.");
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        cameraImageUri = Uri.fromFile(image);
        return image;
    }


    public void previousView(View v){
        //viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        //viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        viewFlipper.showPrevious();
    }

    private void nextView(View v){
        //viewFlipper.setInAnimation(this, R.anim.slide_in_right);
        //viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
        viewFlipper.showNext();
    }

        private void setButtons() {
            // Home page buttons
            addEventButton = (ImageButton) findViewById(R.id.buttonAddEvent);
            orgProfileButton = (ImageButton) findViewById(R.id.buttonOrganizerProfile);
            orgNotificationButton = (ImageButton) findViewById(R.id.notification_button);
            orgHomeButton = (ImageButton) findViewById(R.id.button_organizer_home);
            // Create event page buttons
            reuseCheckInQR = (Button) findViewById(R.id.reuseCheckinQR);
            createEvent = (Button) findViewById(R.id.buttonCreateEvent);
            eventDetailsBack = (ImageButton) findViewById(R.id.buttonBackCreateEvent);
            ImageButton eventDetailsBack = (ImageButton) findViewById(R.id.buttonBackUploadQR);
            uploadQRFromScan = (Button) findViewById(R.id.uploadQRFromScan);
            switchAttendeeLimit = findViewById(R.id.switchAttendeeLimit);
            eventPosterImage = findViewById(R.id.event_poster_image);

        }

        private void setEditText() {
            eventTitleEditText = (EditText) findViewById(R.id.eventNameEditText);
            eventDateEditText = (EditText) findViewById(R.id.eventDateEditText);
            eventAddressEditText = (EditText) findViewById(R.id.eventAddressEditText);
            eventDetailsEditText = (EditText) findViewById(R.id.eventDetailsEditText);
        }



        private void setupAttendeeLimitSwitch() {
            switchAttendeeLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    if (isChecked) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrganizerMainActivity.this);
                        builder.setTitle("Set Attendee Limit");

                        // Set up the input
                        final EditText input = new EditText(OrganizerMainActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int limit = Integer.parseInt(input.getText().toString());
                                    if (limit > 0) {
                                        attendeeLimit = limit;
                                    } else {
                                        // Invalid input, revert the switch to unchecked
                                        switchAttendeeLimit.setChecked(false);
                                        attendeeLimit = Integer.MAX_VALUE;
                                    }
                                } catch (NumberFormatException e) {
                                    switchAttendeeLimit.setChecked(false);
                                    attendeeLimit = Integer.MAX_VALUE;
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                // Revert the switch to unchecked since canceled
                                switchAttendeeLimit.setChecked(false);
                            }
                        });

                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                // Handle the case where the user cancels the dialog (e.g., by pressing the back button)
                                switchAttendeeLimit.setChecked(false);
                            }
                        });

                        builder.show();
                    } else {
                        attendeeLimit = Integer.MAX_VALUE;
                    }
                }
            });
        }


        // "youtube - Implement Barcode QR Scanner in Android studio barcode reader | Cambo Tutorial" - youtube channel = Cambo Tutorial
        private void scanCode () {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Volume up to turn on flash");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);
            barLauncher.launch(options);
        }
        ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

            if (result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrganizerMainActivity.this);
                builder.setTitle("Result");
                builder.setMessage(result.getContents());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reUseQRID = result.getContents().toString();
                        String eventTitle = eventTitleEditText.getText().toString();
                        String eventDate = eventDateEditText.getText().toString();
                        String eventAddress = eventAddressEditText.getText().toString();
                        String eventDetails = eventDetailsEditText.getText().toString();

                        //Integer eventAttendeeLimit = Integer.parseInt(eventAttendeeLimitEditText.getText().toString());
                        organizer.createEventReuseQRCode(eventDetails, eventAddress, 100, eventTitle, reUseQRID);

                        dialog.dismiss();
                    }
                }).show();
            }

        });

    }




