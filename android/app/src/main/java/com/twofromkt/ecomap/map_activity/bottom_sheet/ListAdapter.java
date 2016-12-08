package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.map.MapClusterItem;

import java.util.ArrayList;

import static android.view.View.VISIBLE;
import static com.twofromkt.ecomap.Consts.TRASH_TYPES_NUMBER;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<Pair<MapClusterItem, ? extends Place>> data;
    final MapActivity parentActivity;

    ListAdapter(ArrayList<Pair<MapClusterItem, ? extends Place>> data, MapActivity parentActivity) {
        this.data = data;
        this.parentActivity = parentActivity;
        setHasStableIds(true);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Place p = data.get(position).second;
        holder.name.setText(p.name);

        if (!holder.typesSet) {
            for (int i = 0; i < TRASH_TYPES_NUMBER; i++) {
                ImageView icon = new ImageView(parentActivity);
                Resources res = parentActivity.getResources();
                try {
                    icon.setImageBitmap(BitmapFactory.decodeResource(res,
                            R.mipmap.class.getField("trash" + (i + 1) + "selected").getInt(null)));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                DisplayMetrics metrics = res.getDisplayMetrics();
                int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
                layoutParams.setMargins(0, 0, marginRight, 0);
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, metrics);
                layoutParams.width = layoutParams.height = size;
                holder.iconsLayout.addView(icon, layoutParams);
                icon.setVisibility(VISIBLE);
            }
            holder.typesSet = true;
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.map.focusOnMarker(data.get(position));
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
        final TextView name;
        final LinearLayout iconsLayout;
        final View container;
        boolean typesSet;

        ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            this.name = (TextView) itemView.findViewById(R.id.list_item_name);
            this.iconsLayout = (LinearLayout) itemView.findViewById(R.id.list_item_icons_layout);
        }
    }
}
