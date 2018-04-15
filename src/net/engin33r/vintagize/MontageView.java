package net.engin33r.vintagize;

import java.io.File;
import java.io.FileOutputStream;

import net.engin33r.vintagize.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MontageView extends SurfaceView implements SurfaceHolder.Callback {
	
	private final Bitmap mBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.background_nologo);
	private Bitmap mImage;
	private Bitmap mOverlay;
	private Matrix mMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();
	private int mWidth, mHeight;
	private DrawThread mThread;
	private Bitmap mQueried;
	private Paint mPaint = new Paint();
	
	private boolean mMulti = false; // multi-touch happening
	private PointF centre = new PointF();
	private PointF finger = new PointF();
	private PointF oldpos = new PointF();
	private PointF oldcentre = new PointF();
	private PointF delta = new PointF();
	private double angle = 0; // matrix rotation angle
	private double overlayAng = 0; // overlay angle
	private double oldOverlayAng = 0; // overlay angle
	private double oldAngle = 0; // overlay angle before the rotation began
	private double factor = 1; // zoom factor
	private double oldfactor = 1; // zoom factor
	private double dFinger; // distance between fingers
	private int DRAG = 0;
	private int ZOOM = 1;
	private int ROTATE = 2;
	private int mode; // multi-touch mode
	private int zoomLimit = 50; // change in the distance that makes the code decide to go into zoom mode
	private int angLimit = 5; // change in the angle that makes the code decide to go into rotate mode
	
    protected static Bitmap overlay(Bitmap bmp1, Bitmap bmp2, int x, int y) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, x, y, null);
        return bmOverlay;
    }
    
	public MontageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mThread = new DrawThread(getHolder(),this);
		
		getHolder().addCallback(this);
        setFocusable(true);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (canvas != null) {
			canvas.drawColor(0xfff5e5d5);
			if (mBackground!=null) canvas.drawBitmap(mBackground, null, new Rect(0,0,mWidth,mHeight), null);
			if (mImage!=null) canvas.drawBitmap(mImage,(mWidth-mImage.getWidth())/2,(mHeight-mImage.getHeight())/2,mPaint);
			if (mOverlay!=null) canvas.drawBitmap(mOverlay, mMatrix, mPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mSavedMatrix.set(mMatrix);
				finger.x = (int) x;
				finger.y = (int) y;
				oldcentre.x = centre.x;
				oldcentre.y = centre.y;
				break;
				
			case MotionEvent.ACTION_UP:
				break;
				
			case MotionEvent.ACTION_MOVE:
				if (!mMulti) {
					mMatrix.set(mSavedMatrix);
					if (Math.abs(x-oldpos.x)<50 && Math.abs(y-oldpos.y)<50) {
						centre.x = (float) (oldcentre.x + (x-finger.x));
						centre.y = (float) (oldcentre.y + (y-finger.y));
						mMatrix.postTranslate(x-finger.x, y-finger.y);
					}
					oldpos.x = x;
					oldpos.y = y;
				} else {
					
					delta.x = event.getX(1) - x;
					delta.y = event.getY(1) - y;
					
					double nfactor = Math.sqrt(Math.pow(delta.x,2)+Math.pow(delta.y,2));
					if (mode == DRAG) {
						if (Math.abs(nfactor-dFinger) > zoomLimit) mode = ZOOM;
					}
					
					double fingerAng = Math.atan2(delta.y, delta.x);
					angle = (oldAngle - fingerAng)*(180/Math.PI);
					if (mode == DRAG) {
						if (Math.abs(angle) > angLimit) mode = ROTATE;
					}
					
					if (mode == ROTATE) {
						mMatrix.set(mSavedMatrix);
						mMatrix.postRotate((float) (-angle),centre.x,centre.y);
						overlayAng = oldOverlayAng - angle;
					} else if (mode == ZOOM) {
						mMatrix.set(mSavedMatrix);
						factor = oldfactor + ((float) (nfactor/dFinger)-1);
						mMatrix.postScale((float) (nfactor/dFinger), (float) (nfactor/dFinger), centre.x, centre.y);
					}
				}
				
				break;
			
			case MotionEvent.ACTION_POINTER_DOWN:
				mMatrix.set(mSavedMatrix);
				mMulti = true;
				
				delta.x = event.getX(1) - x;
				delta.y = event.getY(1) - y;
				dFinger = Math.sqrt(delta.x*delta.x + delta.y*delta.y);
				oldAngle = Math.atan2(delta.y, delta.x);
				oldOverlayAng = overlayAng;
				oldfactor = factor;
				break;
				
			case MotionEvent.ACTION_POINTER_UP:
				mMulti = false;
				mode = DRAG;
				mSavedMatrix.set(mMatrix);
				break;
		}
		
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		mWidth = w;
		mHeight = h;
		Log.d("MontageView","size changed to "+w+"x"+h);
		if (mQueried != null) setImage(mQueried); // Redraw queried image
	}
	
	public Uri composePicture() {
		Bitmap targetPicture = Bitmap.createBitmap(mImage.getWidth(),mImage.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(targetPicture);
		
		canvas.drawBitmap(mImage, 0, 0, mPaint);
		
		int ox = (mWidth - mImage.getWidth())/2;
		int oy = (mHeight - mImage.getHeight())/2;
		
		Matrix m = new Matrix();
		m.set(mMatrix);
		m.postTranslate(-ox, -oy);
		
		canvas.drawBitmap(mOverlay, m, mPaint);
		
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
                "/.vintagize";
		File dir = new File(file_path);
		if(!dir.exists()) dir.mkdirs();
		File file = new File(dir, "out.png");
		try {
			file.delete();
			FileOutputStream fOut = new FileOutputStream(file);
			targetPicture.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			Log.e("VintagizeApp","Could not write image!");
			e.printStackTrace();
		}
		
		return Uri.parse("file://"+file_path+"/out.png");
	}
	
	public void queryImage(Bitmap image) {
		mQueried = image;
	}
	
	public void setImage(Bitmap image) {
			
		if (image!=null && mWidth!=0 && mHeight!=0) {
			double imagewidth = image.getWidth(), imageheight = image.getHeight();
			
			double newWidth, newHeight;
			
			if (imagewidth > mWidth || imageheight > mHeight) { // If one of the image's measurements is bigger than the screen, fit it
				if (imagewidth > imageheight && mHeight > mWidth) { // If we have a landscape image in portrait mode, fit it horizontally
					newWidth = mWidth;
					double scale = imagewidth/newWidth;
					newHeight = imageheight/scale;
				} else { // In all other cases, fit it vertically
					newHeight = mHeight;
					double scale = imageheight/newHeight;
					newWidth = imagewidth/scale;
				}
			} else { // If the image is smaller than the screen, do nothing with it
				newHeight = imageheight;
				newWidth = imagewidth;
			}
			
			double xpad = getPaddingLeft()+getPaddingRight();
			double ypad = getPaddingTop()+getPaddingBottom();
			
			newWidth -= xpad;
			newHeight -= ypad;
			
			Bitmap nimage = Bitmap.createScaledBitmap(image,(int) newWidth,(int) newHeight,false);
			
			mImage = nimage;
		}
	}
	
	public void setOverlay(Bitmap overlay) {
		mOverlay = overlay;
		
		centre.x = (float) mOverlay.getWidth()/2;
		centre.y = (float) mOverlay.getHeight()/2;
	}
	
	public void setOverlay(int id) {
		Bitmap bm = BitmapFactory.decodeResource(this.getResources(), id);
		mOverlay = bm;
		mMatrix.reset();
		
		centre.x = (float) mOverlay.getWidth()/2;
		centre.y = (float) mOverlay.getHeight()/2;
	}
	
	public void setPaint(Paint paint) {
		mPaint = paint;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
			
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread = mThread.begin();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.kill();
	}
	
}
