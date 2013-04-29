package com.draw;

import java.util.ArrayList;

import android.graphics.Bitmap;

public abstract class MultiDrawable {
	private float z;

	private float x;

	private float y;
	
	private float height;
	
	private float witdh;
	
	private ArrayList<Bitmap> bitmap;

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

	public ArrayList<Bitmap> getBitmap() {
		return bitmap;
	}

	public void setBitmap(ArrayList<Bitmap> bitmap) {
		this.bitmap = bitmap;
	}

	
	
	
}
