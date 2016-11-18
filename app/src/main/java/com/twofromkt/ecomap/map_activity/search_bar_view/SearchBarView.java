package com.twofromkt.ecomap.map_activity.search_bar_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

import static com.twofromkt.ecomap.map_activity.MapActivity.CATEGORIES_N;

public class SearchBarView extends LinearLayout {

    Button openMenuButton;
    EditText searchBar;
    LinearLayout checkboxes, searchBox;
    MapActivity parentActivity;
    ImageButton[] checkboxButtons;
    ImageButton showChecks;

    public boolean[] chosenCheck;

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
        checkboxes = (LinearLayout) findViewById(R.id.checkboxes);
        searchBox = (LinearLayout) findViewById(R.id.search_box);

        checkboxButtons = new ImageButton[]{
                (ImageButton) findViewById(R.id.trash_checkbox),
                (ImageButton) findViewById(R.id.cafe_checkbox),
                (ImageButton) findViewById(R.id.smth_checkbox)};

        this.parentActivity = parentActivity;

        chosenCheck = new boolean[CATEGORIES_N];
        showChecks = (ImageButton) findViewById(R.id.show_checkboxes);

        setListeners();
    }

    private void setListeners() {
        adapter = new SearchBarAdapter(this);
        searchBar.setOnEditorActionListener(adapter);
        openMenuButton.setOnClickListener(adapter);
        for (ImageButton button : checkboxButtons) {
            button.setOnClickListener(adapter);
        }
        showChecks.setOnClickListener(adapter);
    }

}
