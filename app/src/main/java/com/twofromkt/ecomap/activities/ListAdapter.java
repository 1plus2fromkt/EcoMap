package com.twofromkt.ecomap.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Place;

import java.util.ArrayList;
import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    ArrayList<Place> data; // TODO:private

    public ListAdapter(ArrayList<Pair<Marker, ? extends Place>> data) {
        this.data = new ArrayList<>();
        for (Pair<Marker, ? extends Place> d : data)
            this.data.add(d.second);
        setHasStableIds(true);
    }

    public ListAdapter(ArrayList<? extends Place> data, int a) { // bad thing
        this.data = (ArrayList<Place>) data;
        setHasStableIds(true);
    }

    public ListAdapter(ListAdapter a) {
        this.data = new ArrayList<>(a.data);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place p = data.get(position);
        holder.name.setText(p.name);
        holder.cat.setText(p.information);
    }

//    public void updateData(List<? extends Place> data) {
//        this.data = data; // might be too long (GC and everything)
//        notifyDataSetChanged();
//    }

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
        public ViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView)itemView.findViewById(R.id.list_name_text);
            this.cat = (TextView)itemView.findViewById(R.id.list_category_text);
        }
    }
}
