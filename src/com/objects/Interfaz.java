package com.objects;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.draw.MultiDrawable;
import com.draw.Zone;
import com.newproyectjmb.JuegoView;
import com.newproyectjmb.R;

public class Interfaz extends MultiDrawable{
	JuegoView juegoview;
	private int INTERFAZ=0;
	private int BALA=1;
	private int DUCK=2;
	private int DUCKWHITE=3;
	int[] resourcesDrawable={R.drawable.interfaz,R.drawable.bala,R.drawable.duck,R.drawable.duckwhite};
	
	@Override
	public void draw(Canvas canvas, Paint paint,Zone zone) {
		//Dibujamos las balas que nos quedan en esta minifase
		setX(zone.getX());
		setY(zone.getY());
		
		int numbalas = 3 - juegoview.disparos.size();
		for (int i = 0; i < numbalas; i++) {
			canvas.drawBitmap(listDrawable.get(BALA), 9 + i * 10, 393, paint);

		}
		//	Dibujamos los patos acertados/fallados de esta ronda
		for (int i = 0; i < juegoview.thread.ducksFaileds.length; i++) {
			if (juegoview.thread.pintadoEspecialSiEstaMostrandoResultados()) {
				juegoview.thread.pintandoLosPatosDeLaInterfaz(canvas, i, 100 + i * 12,
						393, paint);
			}
		}
		//Mostramos la Ronda actual en la que se encuentra
		paint.setColor(0xFFCCF543);
		paint.setTextSize(11);
		paint.setTextAlign(Paint.Align.LEFT);
		canvas.drawText("R=" + juegoview.thread.getRound(), 5, 378, paint);
		
		//Mostramos la Puntuación actual
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(juegoview.mContext.getResources().getString(
				R.string.juego_puntuacion), 275, 402, paint);
		canvas.drawText("" + juegoview.thread.getScore(), 280, 412, paint);
		
	}

	public void load(Resources resources,JuegoView juegoview) {
		this.juegoview=juegoview;
		List listDrawable = new ArrayList<Bitmap>();
		List<Bitmap> list= new ArrayList<Bitmap>();
		
		for (int i=0;i<resourcesDrawable.length;i++){
			listDrawable.add(i,BitmapFactory.decodeResource(resources, resourcesDrawable[i] ));
		}

		setHeight(((Bitmap)listDrawable.get(BALA)).getHeight());
//		imagenBala = BitmapFactory.decodeResource(resources, R.drawable.bala);
//		imagenInterfazPatoAcertado = BitmapFactory.decodeResource(resources, R.drawable.duck);
//		imagenInterfazPatoFallado= BitmapFactory.decodeResource(resources, R.drawable.duckwhite);
		
	}

	@Override
	public void load(Resources resources) {
		// TODO Auto-generated method stub
		
	}

}
