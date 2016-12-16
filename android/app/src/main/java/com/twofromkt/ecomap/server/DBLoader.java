package com.twofromkt.ecomap.server;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class DBLoader extends AsyncTaskLoader<ServerResultType> {

    private Context context;
    private boolean isLoading;

    private static String TAG = "DB_LOADER";

    public DBLoader(Context context) {
        super(context);
        this.context = context;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void onStartLoading() {
        super.onStartLoading();
        if (isLoading) {
            Log.d(TAG, "database update already running");
        } else {
            forceLoad();
        }
    }

    @Override
    public ServerResultType loadInBackground() {
        Log.d("DB_LOADER", "started updating database");
        isLoading = true;
        ServerResultType result = Downloader.update(context);
        isLoading = false;
        return result;
    }
}
