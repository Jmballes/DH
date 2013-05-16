package com.draw;

public class Zone {
	private int x;
	private int y;
	private int z;
	private int width;
	private int height;
	public Zone(int x, int y, int z, int width, int height){
		this.x=x;
		this.y=y;
		this.z=z;
		this.width=width;
		this.height=height;
		
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
}
