package com.draw;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class MultiDrawable {
	private float z;

	private float x;

	private float y;
	
	private float height;
	
	private float witdh;
	
	public List<Bitmap> listDrawable;

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWitdh() {
		return witdh;
	}

	public void setWitdh(float witdh) {
		this.witdh = witdh;
	}

	public abstract void draw(Canvas canvas,Paint paint,Zone zone);
	public abstract void load(Resources resources);
	
	
}
