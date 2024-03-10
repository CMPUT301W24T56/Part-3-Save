package com.example.fiftysix;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;

public class AttendeeTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    /**
     * testing whether an attendee is able to go to the profile section
     * testing whether an attendee can edit his / her profile or not **/
    @Test
    public void edit_profile_check(){
        // going to attendee main page
        onView(withId(R.id.buttonAttendee)).perform(click());

        // going to profile section
        onView(withId(R.id.buttonAttendeeProfile)).perform(click());

        // person editing their name, email, and contact info
        // Clear the already written text in all those fields
        onView(withId(R.id.profile_name)).perform(click(), clearText()).perform(typeText("Arsh Arora"));
        onView(withId(R.id.profile_email)).perform(click(), clearText()).perform(typeText("arsharora0388@gmail.com"));
        onView(withId(R.id.profile_phone)).perform(click(), clearText()).perform((typeText("5877784938")));


        // Click the save button and it goes back
        onView(withId(R.id.profile_save)).perform(click());

    }

}