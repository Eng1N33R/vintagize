package net.engin33r.vintagize;

import java.io.Serializable;

import android.graphics.ColorFilter;
import android.graphics.Paint;

public class SerializablePaint extends Paint implements Serializable {

	private static final long serialVersionUID = 311137747318827363L;

	public SerializablePaint() {
		super();
	}

	public SerializablePaint(int flags) {
		super(flags);
	}

	public SerializablePaint(Paint paint) {
		super(paint);
	}
	
	public SerializablePaint fromPaint(Paint paint) {		
		SerializablePaint sp = new SerializablePaint(paint);
		return sp;
	}
	
	public Paint toPaint(SerializablePaint paint) {		
		Paint sp = new Paint(paint);
		return sp;
	}

}
