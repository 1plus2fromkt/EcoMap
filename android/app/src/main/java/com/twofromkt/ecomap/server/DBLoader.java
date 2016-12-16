package com.twofromkt.ecomap.server;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class DBLoader extends AsyncTaskLoader<ServerResultType> {

    private Context context;

    public DBLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ServerResultType loadInBackground() {
        Log.d("DB_LOADER", "started updating database");
        ServerResultType result = Downloader.update(context);
        return result;
    }
}
