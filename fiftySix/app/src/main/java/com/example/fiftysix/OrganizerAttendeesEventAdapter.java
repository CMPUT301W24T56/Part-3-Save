package com.example.fiftysix;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrganizerAttendeesEventAdapter extends RecyclerView.Adapter<OrganizerAttendeesEventAdapter.EventVH> {

    List<Event> eventList;
    private Context context;

    public OrganizerAttendeesEventAdapter(ArrayList<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }


    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_attendee_data_row, parent, false);
        return new EventVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventVH holder, int position) {

        Event event = eventList.get(position);
        holder.attendeeName.setText(event.getEventName());
        holder.phoneNumber.setText(event.getLocation());
        holder.email.setText(event.getDate());
        holder.phoneNumber.setText(event.getDetails());


        String imageUrl = event.getPosterURL();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .into(holder.attendeeImage); // Your ImageView
        }

        boolean isExpandable = eventList.get(position).getExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventVH extends RecyclerView.ViewHolder {

        TextView attendeeName, checkinTime, numberCheckins, phoneNumber, email;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;
        ImageView attendeeImage;


        public EventVH(@NonNull View itemView) {
            super(itemView);

            attendeeName = itemView.findViewById(R.id.attendee_name);
            checkinTime = itemView.findViewById(R.id.checkin_time);
            numberCheckins = itemView.findViewById(R.id.number_checkins);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            email = itemView.findViewById(R.id.email);


            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            attendeeImage = itemView.findViewById(R.id.event_poster_image);


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Event event = eventList.get(getAdapterPosition());
                    event.setExpandable(!event.getExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

}
