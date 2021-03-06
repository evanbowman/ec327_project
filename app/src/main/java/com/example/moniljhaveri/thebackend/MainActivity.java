package com.example.moniljhaveri.thebackend;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;


public class MainActivity extends Activity{

    private static String logtag = "CameraApp8";
    private static int TAKE_PICTURE = 0;
    private Uri imageUri;
    private static int imageGallery_load_image = 1; //this is for accessing the image gallery
    private String selectedImagePath;

    private ImageView TakenPhoto; //this is to bring the photo to the gallery
    TextView textTargetUri;



    private OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.library_button:
                    Intent libraryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(libraryIntent, imageGallery_load_image);
                    break;
                case R.id.camera_button:
                    takePhoto(view);
                    break;
            }
        }
    };
    private void takePhoto(View v) { //this is for the camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pictures.jpeg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);

    };
    @Override
    public void onCreate(Bundle savedInstanceState) { //This is for accessing gallery
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(onClick);

        Button libraryButton = (Button) findViewById(R.id.library_button);

        //get a reference to the image view that the picture will see
        TakenPhoto = (ImageView) findViewById(R.id.image_camera);
        textTargetUri = (TextView) findViewById(R.id.textView);
        libraryButton.setOnClickListener(onClick); // this is the listener for the library
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_main, menu);
       return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            //call parent
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode == Activity.RESULT_OK) { //This is the activity for the camera
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView = (ImageView) findViewById(R.id.image_camera);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                try {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(MainActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "failed to load", Toast.LENGTH_LONG).show();
                    Log.e(logtag, e.toString());

                }}
            case 2: //This is what happens when you click on the library button it goes to the image library 
                if(resultCode == RESULT_OK){
                    Uri galleryUri = data.getData();
                    textTargetUri.setText(galleryUri.toString());
                    Bitmap bitmap;
                    try{
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(galleryUri));
                        TakenPhoto.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }


             }
            }
        }
   }

//    public String getPath(Uri uri1){ //This gets path of the string image
//        String res = null;
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(uri1, projection, null, null, null);
//        if ( cursor.moveToFirst()){;
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            res = cursor.getString(column_index);
//        }
//        cursor.close();
//        return res;
//    }
//}
   