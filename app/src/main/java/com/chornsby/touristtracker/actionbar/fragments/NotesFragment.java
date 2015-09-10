package com.chornsby.touristtracker.actionbar.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.chornsby.touristtracker.data.TrackerContract;
import com.chornsby.touristtracker.notes.NoteDetailActivity;

public class NotesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;
    private static final String[] PROJECTION = {
            TrackerContract.NoteEntry._ID,
            TrackerContract.NoteEntry.COLUMN_TEXT,
    };

    public NotesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        String[] fromColumns = {TrackerContract.NoteEntry.COLUMN_TEXT};
        int[] toViews = {android.R.id.text1};

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                fromColumns,
                toViews,
                0
        );

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
        intent.putExtra(TrackerContract.NoteEntry.TABLE_NAME, ((Long) id).toString());
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                TrackerContract.NoteEntry.CONTENT_URI,
                PROJECTION,
                null,
                null,
                TrackerContract.NoteEntry._ID + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
