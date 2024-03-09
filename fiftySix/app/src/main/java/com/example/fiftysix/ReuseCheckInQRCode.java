
// NOT IN USE can delete file

package com.example.fiftysix;

import com.google.firebase.firestore.FirebaseFirestore;

public class ReuseCheckInQRCode {

    private String organizerID;
    private String newEventID;
    private String qRCodeID;

    public ReuseCheckInQRCode(String organizerID, String newEventID, String qRCodeID) {
        this.organizerID = organizerID;
        this.newEventID = newEventID;

        // Need to ensure event is not currently active.

        FirebaseFirestore.getInstance().collection("Events").document(newEventID).update("checkInQRCode", qRCodeID);
        FirebaseFirestore.getInstance().collection("CheckInQRCode").document(qRCodeID).update("event", newEventID);

    }
}
