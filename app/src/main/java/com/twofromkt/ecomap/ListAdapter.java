package com.twofromkt.ecomap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twofromkt.ecomap.db.Place;

import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private final List<Place> data;
    private final LayoutInflater li;

    public ListAdapter(List<Place> data, LayoutInflater li) {
        this.data = data;
        this.li = li;
        setHasStableIds(true);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(li.inflate(R.layout.search_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place p = data.get(position);
        holder.name.setText(p.name);
        holder.cat.setText(p.information);
    }

    @Override
    public int getItemCount() {
        return 0;
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
