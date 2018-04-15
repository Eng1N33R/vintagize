package net.engin33r.vintagize;

import net.engin33r.vintagize.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SocialActivity extends Activity {
	
	private String caption = "This image has been Vintagized!";
	private Uri image;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_social);
		
		image = (Uri) this.getIntent().getExtras().get("uri");
		try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
			((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(this);

		View promptView = layoutInflater.inflate(R.layout.dialog_caption, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to be the layout file of the alertdialog builder
		alertDialogBuilder.setView(promptView);

		final EditText input = (EditText) promptView.findViewById(R.id.caption);
		
		// setup a dialog window
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								caption = input.getText().toString();
								share();
							}
						})
				.setNegativeButton("Skip",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int id) {
								dialog.cancel();
								share();
							}
						});

		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();
		alertD.setTitle("Enter caption");

		alertD.show();
		
		final ImageButton share = (ImageButton) findViewById(R.id.soc_share);
	    final ImageButton discard = (ImageButton) findViewById(R.id.soc_discard);
	    
		AlertDialog.Builder discardDialogBuilder = new AlertDialog.Builder(this);
		discardDialogBuilder
		.setCancelable(false)
		.setTitle("Are you sure?")
		.setMessage("This will delete the image. There is no way to undo this.")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					getContentResolver().delete(image, null, null);
					SocialActivity.this.finish();
					Intent intent = new Intent(SocialActivity.this, MainActivity.class);
					SocialActivity.this.startActivity(intent);
				}
			})
		.setNegativeButton("Cancel",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,	int id) {
					dialog.cancel();
				}
			});
	    
	    final AlertDialog discardDialog = discardDialogBuilder.create();
		
	    share.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	share.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	share.setBackgroundDrawable(null);
	            	share();
	            }			
				return false;
			}
	    	
	    });
	    
	    discard.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN ) {
					discard.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	discard.setBackgroundDrawable(null);
	            	discardDialog.show();
	            }			
				return false;
			}
	    	
	    });
	}
	
	public void share() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/jpeg");
        sendIntent.putExtra(Intent.EXTRA_STREAM, image);
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, caption);
        startActivity(Intent.createChooser(sendIntent, "Share via"));
	}

	@Override
	public void onBackPressed() {
		
		SocialActivity.this.finish();
		Intent intent = new Intent(SocialActivity.this, MainActivity.class);
		SocialActivity.this.startActivity(intent);
        
	}

}
