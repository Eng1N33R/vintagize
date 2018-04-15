package net.engin33r.vintagize;

import java.io.InputStream;
import net.engin33r.vintagize.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class MontageActivity extends Activity {
	
	private String picUri;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    setContentView(R.layout.activity_montage);
	    
	    MontageView montage = (MontageView) findViewById(R.id.montage1);
	    
	    Bundle b = this.getIntent().getExtras();
	    picUri = b.getString("picture");
	    Uri u = Uri.parse(picUri);
	    
		try {
			InputStream stream = getContentResolver().openInputStream(u);
	        Bitmap bitmap = BitmapFactory.decodeStream(stream);
		    montage.queryImage(bitmap);
		    montage.setOverlay(BitmapFactory.decodeResource(getResources(), R.drawable.mustache01));
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Could not load image", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		final ImageButton accept = (ImageButton) findViewById(R.id.accept);
	    final ImageButton discard = (ImageButton) findViewById(R.id.discard);
	    final ImageButton add = (ImageButton) findViewById(R.id.add);
	    //final ImageButton effect = (ImageButton) findViewById(R.id.effect);
	    final ImageButton select = (ImageButton) findViewById(R.id.select);
	    
	    accept.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	accept.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	 accept.setBackgroundDrawable(null);
	            	 composePicture(null);
	            }

	            return false;
	        }
	    });
	    
	    discard.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	discard.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	discard.setBackgroundDrawable(null);
	        		Intent intent = new Intent(MontageActivity.this, MainActivity.class);
	                startActivity(intent);
	        		finish();
	            }

	            return false;
	        }
	    });


	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	    
	    alertDialogBuilder
			.setTitle("Are you sure?")
			.setMessage("This will merge the images. There is no way to undo this.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							Uri u = ((MontageView) findViewById(R.id.montage1)).composePicture();
							Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(u));
							((MontageView) findViewById(R.id.montage1)).setImage(bitmap);
							selectElement(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	    
		final AlertDialog alertD = alertDialogBuilder.create();
	    
	    add.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	add.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	 add.setBackgroundDrawable(null);
	            	 alertD.show();
	            }

	            return false;
	        }
	    });
	    
	    /*effect.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	effect.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	effect.setBackgroundDrawable(null);
	            	selectEffect(null);
	            }

	            return false;
	        }
	    });*/
	    
	    select.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	select.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	 select.setBackgroundDrawable(null);
	            	 selectElement(null);
	            }

	            return false;
	        }
	    });
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.d("VintagizeApp","Request code: "+requestCode);
		//Log.d("VintagizeApp","rsCode="+resultCode+" (RESULT_OK="+RESULT_OK+")");
    	if (requestCode == 1) {
    		if (resultCode == RESULT_OK) {
    			int id = data.getIntExtra("overlay", 0);
    			MontageView montage = (MontageView) findViewById(R.id.montage1);
    			if (id!=0)
    				montage.setOverlay(id);
    		}
    	}
    	
    	if (requestCode == 2) {
    		if (resultCode == RESULT_OK) {
    			SerializablePaint paint = (SerializablePaint) data.getSerializableExtra("paint");
    			MontageView montage = (MontageView) findViewById(R.id.montage1);
    			if (paint!=null)
    				montage.setPaint(new Paint(paint));
    		}
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.montage_options, menu);
	    return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void selectElement(MenuItem item) {
		//MontageActivity.this.onStop();
		Intent intent = new Intent(MontageActivity.this, ListActivity.class);
		intent.putExtra("mode", "home");
		startActivityForResult(intent, 1);
	}
	
	public void selectEffect(MenuItem item) {
		//MontageActivity.this.onStop();
		Intent intent = new Intent(MontageActivity.this, ListActivity.class);
		intent.putExtra("mode", "effect");
		startActivityForResult(intent, 2);
	}
	
	public void composePicture(View v) {
		Uri u = ((MontageView) findViewById(R.id.montage1)).composePicture();
		
		MontageActivity.this.finish();
		Intent intent = new Intent(MontageActivity.this, SocialActivity.class);
		intent.putExtra("uri", u);
        MontageActivity.this.startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		/*Intent intent = new Intent(MontageActivity.this, MainActivity.class);
        startActivity(intent);
		finish();*/
        
	}
	
}
