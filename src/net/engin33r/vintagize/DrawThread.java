package net.engin33r.vintagize;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
	
	private SurfaceHolder mHolder;
	private MontageView mView;
	private boolean mRunning = false;
	
	public DrawThread(SurfaceHolder holder, MontageView view) {
		mHolder = holder;
		mView = view;
	}
	
	public DrawThread begin() {
		//Log.d("DrawThread","Starting");
		mRunning = true;
		//Log.d("DrawThread",this.getState().toString());
		if (this.getState()==Thread.State.NEW)
			this.start();
		if (this.getState()==Thread.State.TERMINATED) {
			DrawThread thread = new DrawThread(mHolder, mView);
			thread.begin();
			return thread;
		}
		return this;
	}
	
	public void kill() {
		//Log.d("DrawThread","Stopping");
		mRunning = false;
	}
	
	public void run() {
		while (mRunning) {
			super.run();

			Canvas canvas = mHolder.lockCanvas();
			
			mView.draw(canvas);
			
			if (canvas != null) mHolder.unlockCanvasAndPost(canvas);
		}
	}
	
}
