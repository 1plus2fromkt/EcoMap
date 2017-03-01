package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.support.annotation.MainThread;
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
import java.util.Comparator;
import java.util.List;

class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {

    MapActivity parentActivity;
    private List<Util.PlaceWithCoord> data = new ArrayList<>();

    TimeTableAdapter(List<Util.PlaceWithCoord> e, MapActivity act) {
        updateData(e);
        parentActivity = act;
    }

    @MainThread
    void updateData(List<Util.PlaceWithCoord> newData) {
        data.clear();
        for (Util.PlaceWithCoord ec : newData) {
            for (Ecomobile d : ((Ecomobile) ec.place).split()) {
                data.add(new Util.PlaceWithCoord(d, ec.coordinates));
            }
        }
        Collections.sort(data, new Comparator<Util.PlaceWithCoord>() {
            @Override
            public int compare(Util.PlaceWithCoord x, Util.PlaceWithCoord y) {
                Ecomobile xe = (Ecomobile) x.place;
                Ecomobile ye = (Ecomobile) y.place;
                return xe.compareTo(ye);
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timetable_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Ecomobile e = (Ecomobile) data.get(position).place;
        holder.address.setText(e.getAddress());
        holder.date.setText(e.getPeriod());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.map.focusOnMarker(data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View container;
        TextView address, date;

        ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            address = (TextView) container.findViewById(R.id.timetable_address);
            date = (TextView) container.findViewById(R.id.timetable_date_time);
        }
    }
}
