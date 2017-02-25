package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twofromkt.ecomap.DividerItemDecorator;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.bottom_sheet.ListAdapter;

public class OneList extends RelativeLayout {
//public class OneList extends Fragment {
    public ListAdapter adapter;
    public RecyclerView recycler;

    public OneList(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fragment_list, this);
    }

    void attach(BottomSheetView bottomSheetView) {
        recycler = (RecyclerView) findViewById(R.id.search_list);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(llm);
        recycler.addItemDecoration(new DividerItemDecorator(getContext()));
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
//        recycler = (RecyclerView) rootView.findViewById(R.id.search_list);
//        recycler.setHasFixedSize(true);
//        recycler.setAdapter(adapter);
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        recycler.setLayoutManager(llm);
//        recycler.addItemDecoration(new DividerItemDecorator(getContext()));
//
//        return rootView;
//    }
}