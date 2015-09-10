package com.chornsby.touristtracker.notes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.data.TrackerContract;

public class NoteCursorAdapter extends CursorAdapter {

    public NoteCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);

        NoteViewHolder viewHolder = new NoteViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NoteViewHolder viewHolder = (NoteViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new NoteViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder.bind(cursor, context);
    }

    private static class NoteViewHolder {
        private TextView mNoteView;
        private TextView mRelativeTime;

        public NoteViewHolder(View view) {
            mNoteView = (TextView) view.findViewById(R.id.text_note);
            mRelativeTime = (TextView) view.findViewById(R.id.relative_date_time);
        }

        public void bind(Cursor cursor, Context context) {
            mNoteView.setText(cursor.getString(cursor.getColumnIndex(TrackerContract.NoteEntry.COLUMN_TEXT)));

            CharSequence relativeDateTime = DateUtils.getRelativeDateTimeString(
                    context,
                    cursor.getLong(cursor.getColumnIndex(TrackerContract.NoteEntry.COLUMN_TIME)),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0
            );
            mRelativeTime.setText(relativeDateTime);
        }
    }
}
