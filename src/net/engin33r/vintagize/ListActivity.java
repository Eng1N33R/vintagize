package net.engin33r.vintagize;

import java.util.ArrayList;
import java.util.List;

import net.engin33r.vintagize.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class ListActivity extends Activity {
	
	public List<Integer> mustaches = new ArrayList<Integer>();
	public List<Integer> beards = new ArrayList<Integer>();
	public List<Integer> hats = new ArrayList<Integer>();
	public List<Integer> glasses = new ArrayList<Integer>();
	
	public List<Paint> paints = new ArrayList<Paint>();
	public List<String> paintNames = new ArrayList<String>();
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		/*ListActivity.this.finish();
		Intent intent = new Intent(ListActivity.this, MontageActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		ListActivity.this.startActivity(intent);*/
	}
	
	@SuppressLint("NewApi")
	private void buildElements(final List<Integer> list) {
		final ImageButton other = (ImageButton) findViewById(R.id.listother);
		
		/*other.setImageDrawable(getResources().getDrawable(R.drawable.picture_icon));
		
		other.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
	            	other.setBackgroundColor(0xaa2c94bb);
	            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
	            	other.setBackgroundDrawable(null);

	            	ListActivity.this.finish();
	         		Intent intent = new Intent(ListActivity.this, ListActivity.class);
	         		intent.putExtra("mode", "effect");
	         		ListActivity.this.startActivity(intent);
	            	 
	            }

	            return false;
	        }
	    });*/
		
		int width;
		
		Display display = getWindowManager().getDefaultDisplay();
        
        if (android.os.Build.VERSION.SDK_INT >= 13) {
		    Point size = new Point();
		    display.getSize(size);
		    width = size.x;
        } else {
		    width = display.getWidth();
        }
        
		LayoutInflater inflater = getLayoutInflater();
		
		TableLayout layout = (TableLayout) findViewById(R.id.layout);
		
		List<TableRow> rows = new ArrayList<TableRow>();
		
		for (int e=0; e < Math.ceil(list.size()/3d); e++) {
			TableRow tr = new TableRow(this);
			rows.add(tr);
			layout.addView(tr);
			//Log.d("ListActivity","Creating new table row "+e);
		}
		
		for (int e=0; e < list.size(); e++) {
			
			// Initialise the element and the row
			final RelativeLayout element1 = (RelativeLayout) inflater.inflate(R.layout.element, null, false);
     		final int e1 = e;
			TableRow tr = rows.get((int) Math.floor(e/3d));
			element1.setClickable(true);
			element1.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
		            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
		            	element1.setBackgroundColor(0xaa2c94bb);
		            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
		            	element1.setBackgroundDrawable(null);
		         		
		         		Intent intent = new Intent();
		         		intent.putExtra("overlay",list.get(e1));
		         		Log.w("onTouch",""+list.get(e1));
		         		setResult(RESULT_OK, intent);
		         		finish();
		            	 
		            }

		            return false;
		        }
		    });
			
			// Add images and text
			for (int i=0; i < element1.getChildCount(); i++) {
				View child = element1.getChildAt(i);
				int id = child.getId();
				
				if (id == R.id.element_image) { // If the child is the image view, set its image to the current overlay element
					Drawable drw = getResources().getDrawable(list.get(e));
					/*Bitmap bitmap = ((BitmapDrawable) drw).getBitmap();
					Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));*/
					((ImageView) child).setImageDrawable(drw);
					
				}
				
			}
			
			// Finalise by adding to the table row
			TableRow.LayoutParams params = (TableRow.LayoutParams) element1.getLayoutParams();
			if (params==null) params = new TableRow.LayoutParams((int) (width/3d), TableRow.LayoutParams.WRAP_CONTENT);
			
			tr.addView(element1, params);
			
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) {
    		if (resultCode == RESULT_OK) {
    			int id = data.getIntExtra("overlay", 0);
    			
    			Intent intent = new Intent();
         		intent.putExtra("overlay", id);
         		Log.w("ListActivity", ""+id);
         		setResult(RESULT_OK, intent);
         		finish();
    		}
    	}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		for (int i = 1; i <= 12; i++) {
			mustaches.add(getResources().getIdentifier("mustache"+(i<10 ? "0"+i : ""+i), "drawable", this.getPackageName()));
			beards.add(getResources().getIdentifier("beard"+(i<10 ? "0"+i : ""+i), "drawable", this.getPackageName()));
		}
		hats.add(R.drawable.hat01);
		hats.add(R.drawable.hat02);
		hats.add(R.drawable.hat03);
		hats.add(R.drawable.hat04);
		hats.add(R.drawable.hat05);
		hats.add(R.drawable.hat06);
		glasses.add(R.drawable.glasses01);
		glasses.add(R.drawable.glasses02);
		glasses.add(R.drawable.glasses03);
		glasses.add(R.drawable.glasses04);
		glasses.add(R.drawable.glasses05);
		glasses.add(R.drawable.glasses06);
		
		final ColorMatrix matrixA = new ColorMatrix();
		// making image B&W
		matrixA.setSaturation(0);
		
		final ColorMatrix matrixB = new ColorMatrix();
		// applying scales for RGB color values
		matrixB.setScale(1f, .95f, .82f, 1.0f);
		matrixA.setConcat(matrixB, matrixA);
		
		final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixA);
		
		Paint sepia = new Paint();
		sepia.setColorFilter(filter);
		
		paints.add(sepia);
		paintNames.add("None");
		
		paints.add(sepia);
		paintNames.add("B&W");
		
		paints.add(sepia);
		paintNames.add("Sepia");
		
		setContentView(R.layout.activity_list);
		
		String mode = getIntent().getExtras().getString("mode");
		
		Log.d("ListActivity","Entering activity with mode "+mode);
		
		final ImageButton other = (ImageButton) findViewById(R.id.listother);
		
		if (mode.equals("home")) {
			//((ViewGroup)other.getParent()).removeView(other);
			setContentView(R.layout.activity_list_home);
			
			final ImageButton mustaches = (ImageButton) findViewById(R.id.list_mustaches);
			final ImageButton beards = (ImageButton) findViewById(R.id.list_beards);
			final ImageButton hats = (ImageButton) findViewById(R.id.list_hats);
			final ImageButton glasses = (ImageButton) findViewById(R.id.list_glasses);
			
			mustaches.setOnTouchListener(new OnTouchListener() {

		        public boolean onTouch(View v, MotionEvent event) {
		            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
		            	mustaches.setBackgroundColor(0xaa2c94bb);
		            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
		            	mustaches.setBackgroundDrawable(null);	

		        		ListActivity.this.onStop();
		         		Intent intent = new Intent(ListActivity.this, ListActivity.class);
		         		intent.putExtra("mode","mustaches");
		        		startActivityForResult(intent, 1);
		            }

		            return false;
		        }
		    });
			
			beards.setOnTouchListener(new OnTouchListener() {

		        public boolean onTouch(View v, MotionEvent event) {
		            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
		            	beards.setBackgroundColor(0xaa2c94bb);
		            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
		            	beards.setBackgroundDrawable(null);	

		        		ListActivity.this.onStop();
		         		Intent intent = new Intent(ListActivity.this, ListActivity.class);
		         		intent.putExtra("mode","beards");
		        		startActivityForResult(intent, 1);
		            }

		            return false;
		        }
		    });
			
			hats.setOnTouchListener(new OnTouchListener() {

		        public boolean onTouch(View v, MotionEvent event) {
		            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
		            	hats.setBackgroundColor(0xaa2c94bb);
		            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
		            	hats.setBackgroundDrawable(null);	

		        		ListActivity.this.onStop();
		         		Intent intent = new Intent(ListActivity.this, ListActivity.class);
		         		intent.putExtra("mode","hats");
		        		startActivityForResult(intent, 1);
		            }

		            return false;
		        }
		    });
			
			glasses.setOnTouchListener(new OnTouchListener() {

		        public boolean onTouch(View v, MotionEvent event) {
		            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
		            	glasses.setBackgroundColor(0xaa2c94bb);
		            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
		            	glasses.setBackgroundDrawable(null);	

		        		ListActivity.this.onStop();
		         		Intent intent = new Intent(ListActivity.this, ListActivity.class);
		         		intent.putExtra("mode","glasses");
		        		startActivityForResult(intent, 1);
		            }

		            return false;
		        }
		    });
		}
		else if (mode.equals("mustaches")) {			
			Log.d("ListActivity","Begin building element screen");
			buildElements(mustaches);
		}
		else if (mode.equals("beards")) {			
			Log.d("ListActivity","Begin building element screen");
			buildElements(beards);
		}
		else if (mode.equals("hats")) {			
			Log.d("ListActivity","Begin building element screen");
			buildElements(hats);
		}
		else if (mode.equals("glasses")) {			
			Log.d("ListActivity","Begin building element screen");
			buildElements(glasses);
		}
		else if (mode.equals("effect")) {
			setContentView(R.layout.activity_list_effects);
			
			other.setImageDrawable(getResources().getDrawable(R.drawable.mustache_icon));
			
			other.setOnTouchListener(new OnTouchListener() {

		        public boolean onTouch(View v, MotionEvent event) {
		            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
		            	other.setBackgroundColor(0xaa2c94bb);
		            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
		            	other.setBackgroundDrawable(null);

		            	ListActivity.this.finish();
		         		Intent intent = new Intent(ListActivity.this, ListActivity.class);
		         		intent.putExtra("mode","home");
		         		ListActivity.this.startActivity(intent);
		            	 
		            }

		            return false;
		        }
		    });
			
			LinearLayout options = (LinearLayout) findViewById(R.id.listlinear);
			for (int i=0; i < paints.size(); i++) {
				final int id = i;
				
				RelativeLayout option = new RelativeLayout(this);
				LinearLayout.LayoutParams optopt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				
				ImageView pr = new ImageView(this);
				pr.setImageDrawable(getResources().getDrawable(R.drawable.preview));
				
				RelativeLayout.LayoutParams iopt  = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				iopt.setMargins(10,5,0,0);
				
				final TextView txt = new TextView(this);
				txt.setText(paintNames.get(i));
				txt.setTextColor(Color.BLACK);
				txt.setTextSize(20f);
				
				RelativeLayout.LayoutParams topt  = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				topt.setMargins(120,5,0,0);
				
				if (i==0) {
					final ImageButton img = new ImageButton(this);
					img.setBackgroundDrawable(null);
					img.setImageDrawable(getResources().getDrawable(R.drawable.top_rounded));
					img.setOnTouchListener(new OnTouchListener() {

				        public boolean onTouch(View v, MotionEvent event) {
				            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				            	img.setImageDrawable(getResources().getDrawable(R.drawable.top_rounded_press));
								txt.setTextColor(Color.WHITE);
				            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
				            	img.setImageDrawable(getResources().getDrawable(R.drawable.top_rounded));
								txt.setTextColor(Color.BLACK);

				            	Intent intent = new Intent();
				         		intent.putExtra("paint",new SerializablePaint(paints.get(id)));
				         		setResult(RESULT_OK, intent);
				         		finish();
				            	 
				            }

				            return false;
				        }
				    });
					option.addView(img);
				}else if (i>0 && i < paints.size()-1) {
					final ImageButton img = new ImageButton(this);
					img.setBackgroundDrawable(null);
					img.setImageDrawable(getResources().getDrawable(R.drawable.middle_rounded));
					option.addView(img);
					img.setOnTouchListener(new OnTouchListener() {

				        public boolean onTouch(View v, MotionEvent event) {
				            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				            	img.setImageDrawable(getResources().getDrawable(R.drawable.top_rounded_press));
								txt.setTextColor(Color.WHITE);
				            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
				            	img.setImageDrawable(getResources().getDrawable(R.drawable.top_rounded));
								txt.setTextColor(Color.BLACK);

				            	Intent intent = new Intent();
				         		intent.putExtra("paint",new SerializablePaint(paints.get(id)));
				         		setResult(RESULT_OK, intent);
				         		finish();
				            	 
				            }

				            return false;
				        }
				    });
				}else if (i == paints.size()-1) {
					final ImageButton img = new ImageButton(this);
					img.setBackgroundDrawable(null);
					img.setImageDrawable(getResources().getDrawable(R.drawable.bottom_rounded));
					option.addView(img);
					img.setOnTouchListener(new OnTouchListener() {

				        public boolean onTouch(View v, MotionEvent event) {
				            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				            	img.setImageDrawable(getResources().getDrawable(R.drawable.top_rounded_press));
								txt.setTextColor(Color.WHITE);
				            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
				            	img.setImageDrawable(getResources().getDrawable(R.drawable.top_rounded));
								txt.setTextColor(Color.BLACK);

				            	Intent intent = new Intent();
				         		intent.putExtra("paint",new SerializablePaint(paints.get(id)));
				         		setResult(RESULT_OK, intent);
				         		finish();
				            	 
				            }

				            return false;
				        }
				    });
				}

				option.addView(pr, iopt);
				option.addView(txt, topt);
				options.addView(option, optopt);
			}
		}
	}
	
}
