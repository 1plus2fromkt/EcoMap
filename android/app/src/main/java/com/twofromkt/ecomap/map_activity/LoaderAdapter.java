package com.twofromkt.ecomap.map_activity;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.twofromkt.ecomap.db.PlaceResultType;

public class LoaderAdapter implements LoaderManager.LoaderCallbacks<PlaceResultType> {

    @Override
    public Loader<PlaceResultType> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<PlaceResultType> loader, PlaceResultType data) {

    }

    @Override
    public void onLoaderReset(Loader<PlaceResultType> loader) {

    }
}
