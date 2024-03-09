package com.example.fiftysix;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

// Ran out of time, no tests. Git merge messed up our main files and lost so much 3 am before this was due. sorry.
public class OrganizerTest {

    private Context context = ApplicationProvider.getApplicationContext();


    private Organizer mockOrganizer() {
        Organizer orgaznier = new Organizer(context);
        return orgaznier;
    }

    @Test
    void addEvent() {
        Organizer organizer = mockOrganizer();
        organizer.createEventNewQRCode("details1","location1",1,"eventName1","Date1");

    }



}

