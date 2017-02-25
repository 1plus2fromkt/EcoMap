package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.place_types.Ecomobile;
import com.twofromkt.ecomap.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder>{

    MapActivity parentActivity;
    private List<Ecomobile> data;

    public TimeTableAdapter(List<Util.PlaceWithCoord> e, MapActivity act) {
        parentActivity = act;
        data = new ArrayList<>();
        //TODO: this can't be done in UI
        for (Util.PlaceWithCoord ec : e) {
            for (Ecomobile d : ((Ecomobile)ec.place).split()) {
                data.add(d);
            }
        }
        Collections.sort(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timetable_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ecomobile e = data.get(position);
        holder.address.setText(data.get(position).getAddress());
        holder.date.setText(data.get(position).getPeriod());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View container;
        public TextView address, date;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            address = (TextView) container.findViewById(R.id.timetable_address);
            date = (TextView) container.findViewById(R.id.timetable_date_time);
        }
    }
}
