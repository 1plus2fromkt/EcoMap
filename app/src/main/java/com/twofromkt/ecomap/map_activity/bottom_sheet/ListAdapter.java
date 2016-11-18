package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.ArrayList;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<Pair<Marker, ? extends Place>> data;
    final MapActivity act;

    public ListAdapter(ArrayList<Pair<Marker, ? extends Place>> data, MapActivity act) {
        this.data = data;
        this.act = act;
        setHasStableIds(true);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Place p = data.get(position).second;
        holder.name.setText(p.name);
        holder.cat.setText(p.information);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.map.focusOnMarker(data.get(position));
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, cat;
        final View container;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            this.name = (TextView) itemView.findViewById(R.id.list_name_text);
            this.cat = (TextView) itemView.findViewById(R.id.list_category_text);
        }
    }
}
