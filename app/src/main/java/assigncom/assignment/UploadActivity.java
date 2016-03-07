package assigncom.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {
    protected Uri mMediaUri;
    ImageView mDraweeView;
    ImageButton mCancelButton, mUploadButton;
    Bitmap bitmap;
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TEXT = "name";
    public static final String UPLOAD_URL = "http://sidharthsethia.16mb.com/Assignment/upload.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mDraweeView = (ImageView) findViewById(R.id.my_image_view);
        mCancelButton = (ImageButton)findViewById(R.id.camera);
        mUploadButton = (ImageButton)findViewById(R.id.upload);

        mMediaUri = getIntent().getData();

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        UploadActivity.this,
                        MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable())
                    uploadImage();
                else {
                    Toast.makeText(UploadActivity.this, "No Internet Available", Toast.LENGTH_LONG).show();
                }
            }
        });

        bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mMediaUri);
            mDraweeView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadImage(){

        final String image = getStringImage(bitmap);
        final String text = mMediaUri.getPath();
        class UploadImage extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UploadActivity.this,"Please wait...","Uploading",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Log.d("Message",s);
                Toast.makeText(UploadActivity.this, s, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(
                        UploadActivity.this,
                        MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String,String> param = new HashMap<String,String>();
                param.put(KEY_TEXT,text);
                param.put(KEY_IMAGE,image);
                String result = rh.sendPostRequest(UPLOAD_URL, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
