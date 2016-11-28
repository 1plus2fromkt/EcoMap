package com.twofromkt.ecomap.map_activity.search_bar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

public class SearchBarView extends LinearLayout {

    Button openMenuButton;
    EditText searchBar;
    LinearLayout searchBox;
    MapActivity parentActivity;

    private SearchBarAdapter adapter;

    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.search_bar, this);
    }

    public void attach(MapActivity parentActivity) {
        openMenuButton = (Button) findViewById(R.id.menu_button);
        searchBar = (EditText) findViewById(R.id.search_edit);
        searchBar.setCursorVisible(false);
        searchBar.setHint("Search query");
        searchBox = (LinearLayout) findViewById(R.id.search_box);

        this.parentActivity = parentActivity;

        setListeners();
    }

    private void setListeners() {
        adapter = new SearchBarAdapter(this);
        searchBar.setOnEditorActionListener(adapter);
        openMenuButton.setOnClickListener(adapter);
    }

//    @Override
//    public Parcelable onSaveInstanceState() {
//        Parcelable superSaved = super.onSaveInstanceState();
//        return new SavedSearchBar(superSaved);
//    }

//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        SavedSearchBar savedState = (SavedSearchBar) state;
//        super.onRestoreInstanceState(savedState.getSuperState());
//        checkboxes.setVisibility(savedState.getVisibility());
//        chosenCheck = savedState.getChosen();
//        for (int i = 0; i < CATEGORIES_N; i++) {
//            if (chosenCheck[i]) {
//                util.setChosen(i, true, false);
//            }
//        }
//    }

}
