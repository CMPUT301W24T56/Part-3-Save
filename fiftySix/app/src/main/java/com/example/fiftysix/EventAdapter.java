package com.example.fiftysix;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventVH> {

    private List<Event> eventList;
    private boolean isExpandableOnClick;

    public EventAdapter(ArrayList<Event> eventList, boolean isExpandableOnClick) {
        this.eventList = eventList;
        this.isExpandableOnClick = isExpandableOnClick;
    }

    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_row, parent, false);
        return new EventVH(view, isExpandableOnClick);
    }

    @Override
    public void onBindViewHolder(@NonNull EventVH holder, int position) {

        Event event = eventList.get(position);
        holder.codeName.setText(event.getEventName());
        holder.versionTxt.setText(event.getLocation());
        holder.apiLevelTxt.setText(event.getDate());
        holder.descriptionTxt.setText(event.getDetails());

        String imageUrl = event.getPosterURL();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .into(holder.eventImage);
        }

        if (!isExpandableOnClick) {
            holder.expandableLayout.setVisibility(View.GONE);
        } else {
            boolean isExpandable = eventList.get(position).getExpandable();
            holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventVH extends RecyclerView.ViewHolder {

        TextView codeName, versionTxt, apiLevelTxt, descriptionTxt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;
        ImageView eventImage;

        public EventVH(@NonNull View itemView, boolean isExpandableOnClick) {
            super(itemView);

            codeName = itemView.findViewById(R.id.code_name);
            versionTxt = itemView.findViewById(R.id.version);
            apiLevelTxt = itemView.findViewById(R.id.apiLevel);
            descriptionTxt = itemView.findViewById(R.id.description);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            eventImage = itemView.findViewById(R.id.event_poster_image);

            if (isExpandableOnClick) {
                linearLayout.setOnClickListener(v -> {
                    Event event = eventList.get(getAdapterPosition());
                    event.setExpandable(!event.getExpandable());
                    notifyItemChanged(getAdapterPosition());
                });
            }
        }
    }
}
