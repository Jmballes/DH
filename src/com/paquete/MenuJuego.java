package com.paquete;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.newproyectjmb.R;

import edu.union.graphics.IntMesh;
import edu.union.graphics.Model;
import edu.union.graphics.ObjLoader;

public class MenuJuego extends Activity {
	InputStream is;
    GLView gl;
    Button boton1;
    Button boton2;

    public static final int MODO_JUEGO_UN_PATO=1;
	public static final int MODO_JUEGO_DOS_PATOS=2;
    
    /** Called when the activity is first created. */
    @Override

    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		try {

			setContentView(R.layout.surface_view_overlay);

			gl 		= (GLView) findViewById(R.id.glsurfaceview);
			boton1 	= (Button) findViewById(R.id.boton1);
			boton2 	= (Button) findViewById(R.id.boton2);
			
			is = getResources().openRawResource(R.drawable.mapacontexturatricolor);
			ObjLoader ld = new ObjLoader();
			ld.setFactory(IntMesh.factory());
			Model model = ld.load(is);
			model.getFrame(0).getMesh().setTextureFile("textura.png");
			gl.initView(this, model);
			boton1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("modojuego", MODO_JUEGO_UN_PATO);
					intent.setClass(MenuJuego.this, JuegoActivity.class);
					startActivity(intent);
					finish();

				}
			});
			boton2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("modojuego", MODO_JUEGO_DOS_PATOS);
					intent.setClass(MenuJuego.this, JuegoActivity.class);
					startActivity(intent);
					finish();
				}
			});

		} catch (IOException e) {

		}

	}
    public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case 0: {
				boton1.setVisibility(View.VISIBLE);
				boton2.setVisibility(View.VISIBLE);
				break;
			}

			default:
				super.handleMessage(msg);
			}

		}
	};
}