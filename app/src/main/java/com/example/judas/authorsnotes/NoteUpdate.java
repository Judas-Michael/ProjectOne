package com.example.judas.authorsnotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by judas on 10/31/2017.
 */

public class NoteUpdate extends CursorAdapter {


    Context context;

    private static int HASH_COL = 1;
    private static int NOTE_CHAR_COL = 2;

    public NoteUpdate(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.listview_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //TODO Put data from this cursor (represents one row of the database) into this view (the corresponding row in the list)

        TextView productListName = (TextView) view.findViewById(R.id.hashtag_name);
        TextView productListQuantity = (TextView) view.findViewById(R.id.note_char_name);
        productListName.setText(cursor.getString(HASH_COL));
        productListQuantity.setText(cursor.getString(NOTE_CHAR_COL));
    }
}
