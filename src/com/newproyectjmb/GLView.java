package com.newproyectjmb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.union.graphics.Model;

class GLView extends SurfaceView implements SurfaceHolder.Callback {

	private GLThread glThread1;
	Model m;
	Context c;

	public MenuJuego suhas3dNew;

	public GLView(Context context) {

		super(context);
		setFocusable(true);
		c = context;
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);

	}

	public GLView(Context context, AttributeSet attrs) {

		super(context, attrs);
		System.out.println("init");
		setFocusable(true);

		c = context;
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);

	}
   public void setModel(Model m){
	   this.m = m;
   }
   public void initView(MenuJuego suhas3dNew,Model m){
	   this.suhas3dNew=suhas3dNew;
	   this.m=m;
   }
   
   public void surfaceCreated(SurfaceHolder holder) {
      
      // The Surface has been created so start our drawing thread
      

		  glThread1 = new GLThread(this,m);
		  glThread1.start();

      
   }

   public void surfaceDestroyed(SurfaceHolder holder) {
      
      // Stop our drawing thread. The Surface will be destroyed
      // when we return
	   System.out.println("terminado");

		  glThread1.requestExitAndWait();
	      glThread1 = null;
	      System.out.println("terminadosurfaceDestroyed");
   }

   public void surfaceChanged(SurfaceHolder holder, int format,
         int w, int h) {

   }
}

