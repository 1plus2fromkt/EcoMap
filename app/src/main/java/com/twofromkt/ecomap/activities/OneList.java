package com.twofromkt.ecomap.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.activities.ListAdapter;
import com.twofromkt.ecomap.db.Cafe;

public class OneList extends Fragment {
    public ListAdapter a;
    public RecyclerView rv;
    public OneList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public OneList setAdapter(ListAdapter a) {
        this.a = new ListAdapter(a);
        if (rv != null) {
            rv.invalidate();
            rv.setAdapter(a);
        }
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.search_list);
        rv.setHasFixedSize(true);
        for (int i = 0; i < 10; i++)
            a.data.add(new Cafe("Кафе 1", new LatLng(60.043175, 30.409615), "Мое первое кафе",
                    null, "", "656-68-52", "", "www.vk.com"));
        rv.setAdapter(a);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }
}