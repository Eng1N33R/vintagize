package net.engin33r.vintagize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import net.engin33r.vintagize.R;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	int PICTURE_RESULT;
	static int width, height;
	private Uri mImageUri;
	private boolean listInit = false;
	
	private static final int REQUEST_ID = 1;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_home);
	    
	    final ImageButton takePhoto = (ImageButton) findViewById(R.id.takephoto);
	    final ImageButton loadPhoto = (ImageButton) findViewById(R.id.loadphoto);
	    
	    takePhoto.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	takePhoto.setImageResource(R.drawable.takephoto_press);
	            }
	             else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	takePhoto.setImageResource(R.drawable.takephoto);
	            	takePhoto(v);
	            }

	            return false;
	        }
	    });

	    
	    loadPhoto.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	loadPhoto.setImageResource(R.drawable.loadphoto_press);
	            }
	             else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	loadPhoto.setImageResource(R.drawable.loadphoto);
	            	loadPhoto(v);
	            }

	            return false;
	        }
	    });

        
        Display display = getWindowManager().getDefaultDisplay();
        
        if (android.os.Build.VERSION.SDK_INT >= 13) {
		    Point size = new Point();
		    display.getSize(size);
		    width = size.x;
		    height = size.y;
        } else {
		    width = display.getWidth();
		    height = display.getHeight();
        }
        
	}
	
	public boolean hasImageCaptureBug() {
		
	    // list of known devices that have the bug
    	// TODO: Add other devices if necessary
	    ArrayList<String> devices = new ArrayList<String>();
	    devices.add("android-devphone1/dream_devphone/dream");
	    devices.add("generic/sdk/generic");
	    devices.add("vodafone/vfpioneer/sapphire");
	    devices.add("tmobile/kila/dream");
	    devices.add("verizon/voles/sholes");
	    devices.add("google_ion/google_ion/sapphire");
	    devices.add("google/yakju/maguro");
	    devices.add("google/hammerhead/hammerhead");
	    
	    Log.d("VintagizeApp", "Device model: "+android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
	            + android.os.Build.DEVICE);
	    
	    return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
	            + android.os.Build.DEVICE);

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void takePhoto(View view) {
		File photo;
		try {
			photo = File.createTempFile("collage", "jpg");
		} catch (IOException e) {
			Log.v("CollageApp", "Can't create file to take picture!");
	        Toast.makeText(this, "Cannot take picture! Please check SD card!", Toast.LENGTH_LONG).show();
	        return;
		}
		mImageUri = Uri.fromFile(photo);
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (hasImageCaptureBug()) {
			camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp")));
		}else{
			camera.putExtra(MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		this.startActivityForResult(camera, PICTURE_RESULT);
	}
	
	public void loadPhoto(View view) {
		Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_ID);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PICTURE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
               // Display image received on the view
                Uri u = null;
                if (hasImageCaptureBug()) {
                    File fi = new File("/sdcard/tmp");
                    try {
                        u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), fi.getAbsolutePath(), null, null));
                        if (!fi.delete()) {
                            Log.i("VintagizeApp", "Failed to delete " + fi);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                   u = data.getData();
               }
                
                Intent intent = new Intent(MainActivity.this, MontageActivity.class);
                intent.putExtra("picture", u.toString());
                startActivity(intent);
                //finish();
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
            	return;
            }
		}
		
        if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
                Uri u = data.getData();
                
                Intent intent = new Intent(MainActivity.this, MontageActivity.class);
                intent.putExtra("picture",u.toString());
                startActivity(intent);
                //finish();
        }
	}
}