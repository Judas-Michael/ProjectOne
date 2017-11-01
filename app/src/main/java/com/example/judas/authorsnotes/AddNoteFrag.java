package com.example.judas.authorsnotes;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.example.judas.authorsnotes.R.id.new_note_submit;
import static com.example.judas.authorsnotes.R.layout.activity_main;
import static com.example.judas.authorsnotes.R.layout.newnoteadd;

/**
 * Created by judas on 10/25/2017.
 */

public class AddNoteFrag extends AppCompatActivity{

    EditText NewNoteText;
    EditText HashAdd;

    NoteUpdate NotesListAdapter;
    Cursor allProductsCursor;

    Button NewNoteSubmit;
    ImageButton NoteImage;

    private Database dbManager;

    private static final int REQUEST_SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1001;

    // To identify that the camera is returning a result
    private static final int TAKE_PICTURE_REQUEST_CODE = 0;

    // For file storage, where is the current image stored?
    private String mImagePath;

    // The image to be displayed in the app
    private Bitmap mImage;

    // Used in the instance state Bundle, to preserve image when device is rotated
    private static final String IMAGE_FILEPATH_KEY = "image filepath key";

    int id_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newnoteadd);

        dbManager = new Database(this);

        NewNoteText = (EditText)findViewById(R.id.new_note_text);
        HashAdd = (EditText)findViewById(R.id.hashtag_adder);

        NewNoteSubmit = (Button)findViewById(R.id.new_note_submit);
        NoteImage = (ImageButton)findViewById(R.id.note_image);
        allProductsCursor = dbManager.getCursorAll();
        NotesListAdapter = new NoteUpdate(this, allProductsCursor, false);


        NewNoteSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String newName = NewNoteText.getText().toString();
                String newQuantity = HashAdd.getText().toString();

                if ( newName.length() == 0  || newQuantity.length() == 0) {   //regex validation
                    Toast.makeText(AddNoteFrag.this, "Please enter a product name and numerical quantity",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                int quantity = id_count+1;

                if (dbManager.addProduct(newName,quantity)){
                    Toast.makeText(AddNoteFrag.this, "Product added to database", Toast.LENGTH_LONG).show();

                    NotesListAdapter.changeCursor(dbManager.getCursorAll());
                    NewNoteText.getText().clear();
                    HashAdd.getText().clear();
                } else {
                    Toast.makeText(AddNoteFrag.this, newName + " is already in the database",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        NoteImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

    }
    private void takePhoto() {

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check to see if there is a camera on this device.
        if (pictureIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(AddNoteFrag.this, "Your device does not have a camera", Toast.LENGTH_LONG).show();
        }

        else {
            // Create a unique filename for the image
            String imageFilename = "authornotes_" + new Date().getTime();  //Create a unique filename with a timestamp

            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);  // directory to store temp file
            File imageFile = null;
            Uri imageFileUri = null;

            try {
                // Create a temporary file with this name and path
                imageFile = File.createTempFile(imageFilename, ".jpg", storageDirectory);
                mImagePath = imageFile.getAbsolutePath();   // Save path in global variable
                // Create an URI from the path; the Intent will send this to the camera. A URI defines a location and how to access it
                // For example content://com.clara.simplecameraapp/my_images/simple_camera_app_15054908234543141945190112.jpg
                imageFileUri = FileProvider.getUriForFile(AddNoteFrag.this, "com.judas.AuthorsNotes/my_images/", imageFile);

            } catch (IOException ioe) {

                return;   // Will be unable to continue if unable to access storage
            }

            //So if creating the temporary file worked, should have a value for imageFileUri. Include this URI as an extra
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);

            //And then request the camera is launched
            startActivityForResult(pictureIntent, TAKE_PICTURE_REQUEST_CODE);
        }
    }
}
