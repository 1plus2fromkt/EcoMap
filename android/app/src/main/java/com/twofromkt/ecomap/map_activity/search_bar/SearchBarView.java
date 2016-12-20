package com.twofromkt.ecomap.map_activity.search_bar;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

public class SearchBarView extends LinearLayout {

    Button openMenuButton;
    EditText searchBar;
    ProgressBar progressBar;
    LinearLayout searchBox;
    MapActivity parentActivity;

    boolean progressBarShown;

    @IntDef({PROGRESS_BAR_GREEN, PROGRESS_BAR_BLUE})
    public @interface ProgressBarColor {
    }

    public static final int PROGRESS_BAR_GREEN = R.drawable.progress_bar_green;
    public static final int PROGRESS_BAR_BLUE = R.drawable.progress_bar_blue;

    private SearchBarAdapter adapter;

    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.element_search_bar, this);
    }

    public void attach(MapActivity parentActivity) {
        openMenuButton = (Button) findViewById(R.id.menu_button);
        searchBar = (EditText) findViewById(R.id.search_edit);
        searchBar.setCursorVisible(false);
        searchBar.setHint("Search query");
        searchBox = (LinearLayout) findViewById(R.id.search_box);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(GONE);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bar_green));

        this.parentActivity = parentActivity;

        setListeners();
    }

    private void setListeners() {
        adapter = new SearchBarAdapter(this);
        searchBar.setOnEditorActionListener(adapter);
        openMenuButton.setOnClickListener(adapter);
    }

    public void showProgressBar() {
        progressBarShown = true;
        progressBar.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        progressBarShown = false;
        progressBar.setVisibility(GONE);
    }

    public void setProgressBarColor(@ProgressBarColor int color) {
        progressBar.setIndeterminateDrawable(getResources().getDrawable(color));
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superSaved = super.onSaveInstanceState();
        return new SavedSearchBar(superSaved, progressBarShown);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedSearchBar savedState = (SavedSearchBar) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.isProgressBarShown()) {
            showProgressBar();
        }
    }

}