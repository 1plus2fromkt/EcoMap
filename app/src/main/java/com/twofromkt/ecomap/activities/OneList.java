package com.twofromkt.ecomap.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.bottom_sheet_view.ListAdapter;

public class OneList extends Fragment {
    public ListAdapter a;
    public RecyclerView rv;

    public OneList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.search_list);
        rv.setHasFixedSize(true);
        rv.setAdapter(a);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }
}