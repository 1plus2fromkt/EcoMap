package com.twofromkt.ecomap.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.twofromkt.ecomap.R;

public class CategoriesActivity extends Activity implements View.OnClickListener{

    Button[] buttons;
    Button search;
    boolean[] chosen;
    public static final int TRASH_N = 3;
    final float[] ALPHAS = new float[]{(float) 0.6, 1};
    public static final String CHOSEN_KEY = "CHOSEN_KEY";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_categories);
        Intent in = getIntent();
        if (in != null)
            chosen = in.getBooleanArrayExtra(CHOSEN_KEY);
        search = (Button) findViewById(R.id.trash_search);
        buttons = new Button[TRASH_N];
        search.setOnClickListener(this);
        for (int i = 0; i < TRASH_N; i++) {
            try {
                buttons[i] = (Button) findViewById(R.id.class.getField("trash" + (i + 1)).getInt(null));
                buttons[i].setOnClickListener(this);
                setAlpha(i);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setAlpha(int i) {
        buttons[i].setAlpha(ALPHAS[chosen[i] ? 1 : 0]);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstance) {
        super.onRestoreInstanceState(savedInstance);
        if (savedInstance != null) {
            chosen = savedInstance.getBooleanArray(CHOSEN_KEY);
            for (int i = 0; i < TRASH_N; i++) {
                setAlpha(i);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == search) {
            Intent result = new Intent();
            result.putExtra(CHOSEN_KEY, chosen);
            setResult(Activity.RESULT_OK, result);
            finish();
        }
        for (int i = 0; i < TRASH_N; i++) {
            if (v == buttons[i]) {
                chosen[i] = !chosen[i];
                setAlpha(i);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBooleanArray(CHOSEN_KEY, chosen);
    }
}
