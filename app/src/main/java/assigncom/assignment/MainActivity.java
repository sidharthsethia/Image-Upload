package assigncom.assignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageButton mTakePhoto,mUploadPhoto;

    protected Uri mMediaUri;

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 1;



    // Constant declaration for file size type conversions
    public static final int FILE_SIZE = 1024 * 1024 * 10;// 10 MB


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTakePhoto = (ImageButton)findViewById(R.id.camera);
        mUploadPhoto = (ImageButton)findViewById(R.id.upload);




        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePhotoIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // Using mMediaUri
                mMediaUri = getOutputMediaUri();

                if (mMediaUri == null) {
                    // Display error
                    Toast.makeText(MainActivity.this,
                            "Problem in accessing the device external storage",
                            Toast.LENGTH_LONG).show();
                } else {
                    takePhotoIntent
                            .putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                    // Starting the activity for obtaining the result(ie the
                    // image)
                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                }

            }
        });

        mUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                // Set type of file to choose ie only photos
                choosePhotoIntent.setType("image/*");
                startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_PHOTO_REQUEST) {
                // Choose photo or video from gallery or recent(android 4.4)
                if (data == null) {
                    // Show the user an error message
                    Toast.makeText(MainActivity.this,
                            "Sorry,there was an error", Toast.LENGTH_LONG)
                            .show();
                } else {
                    // Data is not null
                    mMediaUri = data.getData();
                }


            } else {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                // Set Uri of the intent
                mediaScanIntent.setData(mMediaUri);
                // Send Broadcast to the gallery application
                sendBroadcast(mediaScanIntent);
            }



            // Starts Upload activity
            Intent uploadingIntent = new Intent(this, UploadActivity.class);
            // Transfer uri as data to the recipients activity.
            uploadingIntent.setData(mMediaUri);

            // Start the Activity
            startActivity(uploadingIntent);

        } else if (resultCode != RESULT_CANCELED) {
            // Show the user error message
            Toast.makeText(this, "Sorry there was an error", Toast.LENGTH_LONG)
                    .show();

        }
    }

    // Method Definition of getOutputMediaUri
    private Uri getOutputMediaUri() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            // Return Uri
            // 1.Get the external storage directory
            // Declare the napplication name
            String appname = MainActivity.this.getString(R.string.app_name);
            File MediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appname);
            // 2.Create our subdirectory
            // Check for existence of MediaStorageDir
            if (!MediaStorageDir.exists()) {
                // mkdirs() returns a boolean value

                if (!MediaStorageDir.mkdirs()) {
                    // Logging exception
                    Log.e(TAG, "Failed to create directory");
                    // Sets the Uri = null
                    return null;
                }
            }
            // 3.Create file-name
            // 4.Create file
            File mediaFile;
            // Date declaration
            Date now = new Date();
            // Declaring the timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.US).format(now);
            // Declaring the path and initializing it to the path of the
            // directory
            // Separator for defining the default file types
            String path = MediaStorageDir.getPath() + File.separator;
            // We need diff. file names and extensions for img and vid ,
            // hence if condition

            mediaFile = new File(path + "IMG_" + timestamp + ".jpg");

            // For debugging purposes, storing the file Uri
            Log.d(TAG, "File" + Uri.fromFile(mediaFile));
            // 5.Return the file's Uri
            return Uri.fromFile(mediaFile);
        } else {
            // No mounted storage
            return null;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        // Defining an if-block
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }

    }
}
