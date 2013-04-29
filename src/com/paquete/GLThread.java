/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
***/
package com.paquete;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;



import edu.union.graphics.FixedPointUtils;
import edu.union.graphics.Mesh;
import static edu.union.graphics.FixedPointUtils.ONE;
import edu.union.graphics.Model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;




class GLThread extends Thread {
	private final GLView view;
	private boolean done = false;

	GLThread(GLView view, Model m) {
		this.view = view;
		this.m = m;
	}

	EGLContext glc;
	public int[] Sonidos_de_musica = { R.raw.introgeneral };

	/**
	 * Objeto encargado de reproducir los sonidos que solicitemos
	 */

	Sonido_Musica efectos_de_musica;

	@Override
	public void run() {

		// Initialize OpenGL
		// ----------------------------------------------------------

		EGL10 egl = (EGL10) EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

		int[] version = new int[2];
		egl.eglInitialize(display, version);

		int[] configSpec = { EGL10.EGL_RED_SIZE, 5, EGL10.EGL_GREEN_SIZE, 6,
				EGL10.EGL_BLUE_SIZE, 5, EGL10.EGL_DEPTH_SIZE, 16,
				EGL10.EGL_NONE };

		EGLConfig[] configs = new EGLConfig[1];
		int[] numConfig = new int[1];
		egl.eglChooseConfig(display, configSpec, configs, 1, numConfig);
		EGLConfig config = configs[0];

		glc = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, null);

		EGLSurface surface = egl.eglCreateWindowSurface(display, config, view.getHolder(), null);
		egl.eglMakeCurrent(display, surface, surface, glc);

		// -----------------------------------------------------------------------------------

		GL10 gl = (GL10) (glc.getGL());
		init(gl);
		InitModel(m, gl);
		efectos_de_musica = new Sonido_Musica(Sonidos_de_musica, view.c);
		efectos_de_musica.prepareSounds();
		efectos_de_musica.playSonido(0);
		// Loop until asked to quit
		while (!done) {
			if (state == ESTADO_INTRO) {
				indicee += 1.8f;
				if (indicee > 200) {
					state = ESTADO_REPETICION;
					view.suhas3dNew.mHandler.sendEmptyMessage(0);
				}
			} else {
				indicee += 1f;
			}

			drawModel(gl);
         
         egl.eglSwapBuffers(display, surface);

         // Error handling
         if (egl.eglGetError() == EGL11.EGL_CONTEXT_LOST) {
            Context c = view.getContext();
            if (c instanceof Activity) {
               ((Activity) c).finish();
            }
         }
         
      }
      

      // Free OpenGL resources
      egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, 
            EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
      egl.eglDestroySurface(display, surface);
      egl.eglDestroyContext(display, glc);
      egl.eglTerminate(display);
      
   }
    private void init(GL10 gl) {
      // Define the view frustrum
      gl.glViewport(0, 0, view.getWidth(), view.getHeight());
      gl.glMatrixMode(GL10.GL_PROJECTION);
      gl.glLoadIdentity();
      float ratio = (float) view.getWidth() / view.getHeight();
      GLU.gluPerspective(gl, 45.0f, ratio, 1, 100f); 
      gl.glShadeModel(GL10.GL_SMOOTH);
	  
	  gl.glClearDepthf(1.0f);
	  gl.glEnable(GL10.GL_DEPTH_TEST);
	  gl.glDepthFunc(GL10.GL_LEQUAL);
	  gl.glEnable(GL10.GL_LIGHTING);
	  gl.glEnable(GL10.GL_LIGHT0);
	  gl.glEnable(GL10.GL_NORMALIZE);
	  gl.glLightxv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
	  gl.glLightxv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
	  gl.glLightxv(GL10.GL_LIGHT0, GL10.GL_POSITION, pos, 0);

	  gl.glEnable(GL10.GL_CULL_FACE);
      
   }

   float distance=10;
   float zz=0;
   float indicee=0;
   byte state=0;
   private final static byte ESTADO_INTRO=0;
   private final static byte ESTADO_REPETICION=1;
   
   
  	protected void drawModel(GL10 gl1) {
  		GL11 gl = (GL11)gl1;
  		
  		gl.glClearColor(0.19f,0.81f,0.98f,255);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);


		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (state==ESTADO_INTRO){
	    	angle=(3.14f*indicee)/160;
	    	distance=(1*indicee/160)+8;
	    	zz=(3.14f*indicee/160)+1;
		}else{
			angle=(3.14f*indicee)/160;

		}

		float posicionCamaraX = (float)(Math.cos(angle) * distance);// + xx;
		float posicionCamaraY = (float)(Math.sin(angle) * distance);// + yy;
	    
		GLU.gluLookAt(gl, posicionCamaraX, posicionCamaraY, zz,
				0, 0, 0,
				0, 0, 1);
		
		gl.glTranslatef(0,0,0);


		gl.glVertexPointer(3,GL10.GL_FIXED, 0, vertices);
		gl.glNormalPointer(GL10.GL_FIXED,0, normals);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
		gl.glDrawElements(GL10.GL_TRIANGLES, verts, GL10.GL_UNSIGNED_SHORT, indices);


	}
   

	
	
   
   public void requestExitAndWait() {
      // Tell the thread to quit
      done = true;
      try {
         join();
      } catch (InterruptedException ex) {
         // Ignore
      }
   }
   
   
   protected static int loadTexture(GL10 gl, Bitmap bmp) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight()*bmp.getWidth()*4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();

		for (int y=0;y<bmp.getHeight();y++)
			for (int x=0;x<bmp.getWidth();x++) {
				ib.put(bmp.getPixel(x,y));
			}
		ib.position(0);
		bb.position(0);

		int[] tmp_tex = new int[1];

		gl.glGenTextures(1, tmp_tex, 0);
		int tx = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tx);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.getWidth(), bmp.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		return tx;
	}
   
   public void InitModel(Model m, GL10 gl)
   {
		ByteBuffer bb;

		Mesh msh = m.getFrame(0).getMesh();



		
		verts = msh.getFaceCount()*3;

		bb = ByteBuffer.allocateDirect(verts*3*4);
		bb.order(ByteOrder.nativeOrder());
		vertices = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts*3*4);
		bb.order(ByteOrder.nativeOrder());
		normals = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts*2*4);
		bb.order(ByteOrder.nativeOrder());
		texCoords = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts*2);
		bb.order(ByteOrder.nativeOrder());
		indices = bb.asShortBuffer();

		short ct = 0;
		for (int i=0;i<msh.getFaceCount();i++) {
			int[] face = msh.getFace(i);
			int[] face_n = msh.getFaceNormals(i);
			int[] face_tx = msh.getFaceTextures(i);
			for (int j=0;j<3;j++) {
				int[] n = msh.getNormalx(face_n[j]);
				int[] v = msh.getVertexx(face[j]);
				for (int k=0;k<3;k++) {
					vertices.put(v[k]);
					normals.put(n[k]);
				}
				int[] tx = msh.getTextureCoordinatex(face_tx[j]);
				texCoords.put(tx[0]);
				texCoords.put(tx[1]);				
				indices.put(ct++);
			}
		}
		vertices.position(0);
		normals.position(0);
		texCoords.position(0);
		indices.position(0);
		
		gl.glTexCoordPointer(2,GL10.GL_FIXED,0,texCoords);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		if (msh.getTextureFile() != null) {
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			tex = loadTexture(gl, BitmapFactory.decodeResource(	view.getContext().getResources(),R.drawable.textura));
		}
   }
   

   private Model m;


	int tex;
	int verts;
	
	float angle;
	
	int lightAmbient[] = new int[] { FixedPointUtils.toFixed(0.2f), 
									 FixedPointUtils.toFixed(0.3f), 
									 FixedPointUtils.toFixed(0.6f), ONE };
	int lightDiffuse[] = new int[] { ONE, ONE, ONE, ONE };


	int[] pos = new int[] {0,20<<16,20<<16, ONE};


	private IntBuffer vertices;
	private IntBuffer normals;
	private IntBuffer texCoords;
	private ShortBuffer indices;
   
}
