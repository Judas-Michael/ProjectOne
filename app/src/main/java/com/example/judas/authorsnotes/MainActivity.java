package com.example.judas.authorsnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import static com.example.judas.authorsnotes.Database.HASH_COL;
import static com.example.judas.authorsnotes.NoteBase.DB_TABLE;
import static com.example.judas.authorsnotes.R.layout.activity_main;
import static com.example.judas.authorsnotes.R.layout.newnoteadd;

public class MainActivity extends AppCompatActivity {


    NoteUpdate NotesListAdapter;
    Cursor allProductsCursor;
    ListView allNotesListView;
    EditText SearchNotes;
    Button NewNote;
    Button SearchNotesButton;

    private Database dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        dbManager = new Database(this);
        allProductsCursor = dbManager.getCursorAll();
        NotesListAdapter = new NoteUpdate(this, allProductsCursor, false);
        SearchNotes = (EditText)findViewById(R.id.search_notes);
        allNotesListView = (ListView)findViewById(R.id.all_Notes_Listview);
        SearchNotesButton = (Button) findViewById(R.id.note_search_button);
        NewNote = (Button) findViewById(R.id.new_note_button);
        allNotesListView.setAdapter(NotesListAdapter);

        NewNote.setOnClickListener(new View.OnClickListener(){

                                             @Override
                                             public void onClick(View v){
                                                 Intent myIntent = new Intent(MainActivity.this,AddNoteFrag.class);
                                                 startActivity(myIntent);
                                             }

                                         });


        SearchNotesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String searchName = SearchNotes.getText().toString();
                if ( searchName.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter a hashtag to search for",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                int quantity = dbManager.getQuantityForProduct(searchName);

                if (quantity == -1) {
                    //Product nt found
                    Toast.makeText(MainActivity.this, "Hashtag #" + searchName + " not found",
                            Toast.LENGTH_LONG).show();
                } else {

                    return db.query(DB_TABLE, null, null, null, null, null, HASH_COL);
                }
            }
        });


        allNotesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

            Cursor cursor = (Cursor) NotesListAdapter.getItem(position);

            String name = cursor.getString(1);

            // We can delete by id, no problem, so could simply call dbManager.deleteProduct(id)
            // In this case, we'd like to show a confirmation dialog
            // with the name of the product, so need to get some data about this list item
            // Want the data? Need to call getItem to get the Cursor for this row

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete")
                    .setMessage("Delete " + name + "?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user clicks ok, then delete
                            dbManager.deleteProduct(id);
                            Toast.makeText(MainActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
                            NotesListAdapter.changeCursor(dbManager.getCursorAll());
                        }
                    }).setNegativeButton(android.R.string.cancel, null) // no negative buttn. do nothing on deletion
                    .create().show();
            return false;
        }
    });


}}



