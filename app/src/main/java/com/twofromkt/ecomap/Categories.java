package com.twofromkt.ecomap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Categories extends Activity implements View.OnClickListener{

    Button[] buttons;
    Button search;
    boolean[] chosen;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.categories);
        search = (Button) findViewById(R.id.trash_search);
        search.setOnClickListener(this);
        chosen = new boolean[]{true};
    }

    @Override
    public void onClick(View v) {
        if (v == search) {
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            intent.putExtra("CHOSEN_KEY", chosen);
            startActivity(intent);
        }
    }
}
