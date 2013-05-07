/*
 * Copyright (C) 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.paquete;






import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.draw.Drawable;
import com.draw.ObjetoAnimable;
import com.newproyectjmb.R;


public class JuegoView extends SurfaceView implements SurfaceHolder.Callback ,SensorListener 	 {

    

    public class JuegoThread extends Thread {
        JuegoActivity j;
        public void setActivity(JuegoActivity j){
        	this.j= j;
        }
    	/** Valor que contendra el Alto de nuestra pantalla*/
    	public int mCanvasHeight = -1;
    	
    	/** Valor que contendra el Ancho de nuestra pantalla*/
        public int mCanvasWidth = -1;

        
    	/** Vector que contendra los patos de una minifase.*/
        private Vector enemys=new Vector();
        

        

        
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;



        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;





        public JuegoThread(SurfaceHolder surfaceHolder, Context context
                ) {
            mSurfaceHolder = surfaceHolder;
            mContext = context;
            
        }

        public long lastTime;
        public int timeAccum;
        public static final int GAP_PROCESS = 10; //tiempo que pasa entre cada proceso (ms)
        public static final int MAX_TIME_ALLOWED_BETWEEN_FRAMES = GAP_PROCESS*2;
        
        long getTime(){
            long currentTime = System.currentTimeMillis();
            long ret = currentTime - lastTime;
            lastTime = currentTime;

            return (ret < MAX_TIME_ALLOWED_BETWEEN_FRAMES) ? ret : MAX_TIME_ALLOWED_BETWEEN_FRAMES;		
         }
        
        public boolean paint = false;
        
        @Override
        public void run() {
        
          lastTime = System.currentTimeMillis();
          timeAccum = 0;
          mRun = true;
          
          while(mRun){
             
             paint = false;
             timeAccum += getTime();
             Canvas canvas = null;

             try {
               canvas = mSurfaceHolder.lockCanvas(null);
               while(timeAccum >= GAP_PROCESS && mRun){
                   paint = true;
                   procesoJuego();
                   timeAccum -= GAP_PROCESS;
               }
                
                if(paint){
               	 doDraw(canvas);
                }
             }
             catch (Exception e) {
 				System.out.println(e);
 				}
             finally {
               if (canvas != null)
                 mSurfaceHolder.unlockCanvasAndPost(canvas);
             }

             
             try {Thread.sleep(5);} catch (InterruptedException ex) {ex.printStackTrace();}
          }
        }




        /**
         * Establece si el thread se esta ejecutando o no.
         */
        
        public void setRunning(boolean b) {
            mRun = b;
        }
        
        /**
         * Vector que contendra los eventos cuando el usuario toca sobre la pantalla.
         */
        Vector eventosRecibidos=new Vector();

        /**
         * Añade una solicitud de disparo, esta sera procesada en el thread, que determinara
         * si se acepta el disparo, dependiendo de si tiene balas suficientes.
         */
        
        public void aniadirSolicitudDisparo(int x,int y){
        	Disparo d=new Disparo(x,y);
        	eventosRecibidos.addElement(d);
        }
        
        /**
         * Procesa las solicitudes de disparo, y las va eliminando despues.
         */
        
        public void procesarSolicitudesDeDisparo(){
        	while(eventosRecibidos.size()>0){
        		comprobandoDisparo((Disparo) (eventosRecibidos.elementAt(0)));
        		eventosRecibidos.remove(0);
        	}
        }

        /**
         * Función encargada de procesar un disparo. Primero detectara si aun quedan balas, y si es asi, mostrará
         * un efecto de sonido y otro de vibración (en caso de que esten activados). Tras ello, comprobará si ha
         * colisionado con uno de nuestros patos, siempre que este en el estado de movimiento. Si ha colisionado,
         * obtendremos una bonificacion e incrementaremos la puntuación actual, y actualizaremos la interfaz de patos
         * acertados.
         * 
         * @param disparo
         */
        public void procesandoDisparo(Disparo disparo){
        	boolean duck=false;
        	//	Si hay menos de 3 disparos
        	if (disparos.size()<3){
        		// Añadimos uno
        		disparos.add(disparo);
        		//	Solicitamos efecto de sonido
        		solicitarSonido(SONIDO_DISPARO);
        		//	Solicitamos efecto de vibracion
        		solicitarVibraccion();
        		
        		//Recorremos los patos para ver si han colisionado con nuestro disparo
        		for (int i=0;i<enemys.size();i++){
        			Pato pato=(Pato)enemys.elementAt(i);
        			//	Si nuestro personaje esta moviendose
        			if (pato.getEstadoActual()==Pato.STATE_MOVE){
        				//	Detectamos si nuestro disparo ha colisionado con el pato
        				if (acertoSobreElPato(pato,disparo)){
        					//	Añadimos una Bonificación
        					bonificaciones.add(new Bonificacion(disparo.x,disparo.y,VALORES_DE_BONIFICACIONES[disparos.size()-1]));
        					//	Incrementamos la puntuación
        					incScore(VALORES_DE_BONIFICACIONES[disparos.size()-1]);
        					//	Iniciaremos el ciclo de ser disparado
        					pato.iniciarCicloDisparado();
        					//	Actualizamos la interfaz.
        					ducksFaileds[pato.getID()]=true;
        					if (patosTodaviaVolando()==0){
        						efectos_de_musica.pararSonido(MUSICA_VOLAR);
        					}
        					duck=true;
        					break;
        				}
        			}
        		}

        	}
        	//Si era nuestro ultimo disparo y fallamos
        	if (!duck && disparos.size()==3){
        		// Hacemos que el resto de los patos huyan.
        		hacerHuirATodosLosPatos();
        	}
        }
        /**
         * Devuelve si el disparo colisiona con el pato. Tiene en cuenta que se calculara de forma
         * distinta dependiendo el modo de control
         * @param pato
         * @param disparo
         * @return
         */
        private boolean acertoSobreElPato(Pato pato,Disparo disparo){
        	return control==CONTROL_TOUCH_MODE && 
        	estaPuntoEnCuadrado((int)pato.getX(),(int)pato.getY(),(int)pato.getX()+Pato.kSpriteSize,(int)pato.getY()+Pato.kSpriteSize,disparo.x,disparo.y) 
					||
			control==CONTROL_MOVE_MODE &&
			isIntersectingRect((int)pato.getX(),(int)pato.getY(),Pato.kSpriteSize,Pato.kSpriteSize,
			disparo.x,disparo.y,realPointSize,realPointSize);
        }
        
        /**
         * Test for a collision between two rectangles using plane exclusion.
         * @return True if the rectangle for from the b coordinates intersects those
         * of a.
         */
         public final boolean isIntersectingRect(int ax, int ay,int aw, int ah,int bx, int by,int bw, int bh){
            if (by + bh < ay || // is the bottom b above the top of a?
                by > ay + ah || // is the top of b below bottom of a?
                bx + bw < ax || // is the right of b to the left of a?
                bx > ax + aw)   // is the left of b to the right of a?
               return false;
            return true;
         }
        /**
         * Las comprobaciones de disparos se haran si estamos en estado de Juego
         * @param disparo
         */
        public void comprobandoDisparo(Disparo d)
        {
        	switch (estadoJuego) {
			case JUEGO_ESTADO_PLAY :
				procesandoDisparo(d);
				break;
			}
          
        }

        /**
         * Deteca colisión entre una caja que se encuentra entre los puntos A1(ax,ay) y A2(ax2,ay2); y un punto B
         * (bx,by).
         * 
         * @param ax
         * @param ay
         * @param ax2
         * @param ay2
         * @param bx
         * @param by
         * @return true si hay colisión.
         */
		public final boolean estaPuntoEnCuadrado(int ax, int ay, int ax2, int ay2,
				int bx, int by) {
			return (bx > ax && bx < ax2 && by > ay && by < ay2);
		}

		/**
		 * 	Inicia la vibración del movil durante 500 milisegundos siempre que dicha opción este
		 * 	habilitada por el usuario.
		 */
		public void solicitarVibraccion(){
        	if (vibracion){
        		Vibrator vibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
        		vibrator.vibrate(500);
        	}
        }


        int indicePatos=0;
        
        /**
         * Dibuja un cuadrado con reborde.
         * @param canvas
         */
        private void drawCuadroCentral(Canvas canvas){
        	float[] outerR = new float[] { 12, 12, 12, 12, 12, 12, 12, 12 };
            RectF   inset = new RectF(2, 2, 2, 2);

            ShapeDrawable mDrawables = new ShapeDrawable(new RoundRectShape(outerR, inset,
          		  outerR));

            ShapeDrawable mDrawables2 = new ShapeDrawable(new RoundRectShape(outerR, null,
          		  outerR));
            mDrawables.getPaint().setColor(0xFFCCF543);

            mDrawables.setBounds((canvas.getWidth()/2)-60, (canvas.getHeight()/2)-25, (canvas.getWidth()/2)+60, (canvas.getHeight()/2)+25);
            mDrawables2.setBounds((canvas.getWidth()/2)-60, (canvas.getHeight()/2)-25, (canvas.getWidth()/2)+60, (canvas.getHeight()/2)+25);
            mDrawables2.draw(canvas);
            mDrawables.draw(canvas);
        }
        
        private void doDraw(Canvas canvas) {
			Paint paint = new Paint();

			

			if (estadoJuego > JUEGO_ESTADO_INICIO) {

				//	Pintamos el fondo azul del cielo
				paint.setColor(Color.argb(0xFF, 0x32, 0xD0, 0xFD));
				canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
						paint);

				//	Pintamos los pajaros
				for (int i = 0; i < enemys.size(); i++) {
					Pato e = (Pato) enemys.elementAt(i);
					e.paint(canvas, paint);
				}
				
				// Pintamos los Disparos
				pintarDisparos( canvas,  paint);
				
				// Pintamos las Bonificaciones
				pintarBonificaciones( canvas,  paint);

				// El perro tendremos que pintarlo antes que los arbustos decorativos en dos casos:
				// 1. Al finalizar una minifase, tanto si la ha ganado como si la ha perdido.
				// 2. Durante la Intro, cuando el perro esta en su ultima fase del salto.
				if (sePintaPerroAntesDeArbustos()) {
						perro.paint(canvas, paint);
				}
				// Dibujamos los arbustos decorativos
				canvas.drawBitmap(imagenFondo, 0, mCanvasHeight
						- imagenFondo.getHeight(), paint);
				
				// Dibujamos al perro despues de los arbustos decorativos, solo durante la intro.
				if (sePintaPerroDespuesDeArbustos()) {
						perro.paint(canvas, paint);

				}
				
				pintarInterfaz( canvas,  paint);

				
				
				pintarInformacionEnCiertosEstados( canvas,  paint);

				
				
				
				paintPuntero(canvas,paint);
				//canvas.drawText(timeGame+"", 50, 50, paint);
			}

				//invalidate();



			

		}
        private void pintarInformacionEnCiertosEstados(Canvas canvas, Paint paint){
        	// En caso de que acierte todos los patos de una ronda, mostraremos
			//	una pantalla mostrando un logotipo
			if (estadoJuego == JUEGO_ESTADO_PERFECT) {
				drawCuadroCentral(canvas);

				paint.setColor(0xFFCCF543);
				paint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText("PERFECT", canvas.getWidth() / 2, (canvas
						.getHeight() / 2) - 5, paint);
				canvas.drawText("30.000", canvas.getWidth() / 2, (canvas
						.getHeight() / 2) + 5, paint);
			}else if (estadoJuego == JUEGO_ESTADO_INTRO) {
				drawCuadroCentral(canvas);

				paint.setColor(0xFFCCF543);
				paint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(mContext.getResources().getString(
						R.string.juego_ronda)+" "+getRound(), canvas.getWidth() / 2, (canvas
						.getHeight() / 2) , paint);

			}else if (estadoJuego == JUEGO_ESTADO_PAUSA) {	
				//Si se encuentra en el estado de pausa, mostramos un letrero que lo indique.
				drawCuadroCentral(canvas);
				paint.setColor(0xFFCCF543);
				paint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(mContext.getResources().getString(
						R.string.menu_pausa), canvas.getWidth() / 2,
						(canvas.getHeight() / 2), paint);
			}else if(estadoJuego == JUEGO_ESTADO_PARTIDATERMINADA){
				drawCuadroCentral(canvas);
				paint.setColor(0xFFCCF543);
				paint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(mContext.getResources().getString(
						R.string.juego_gamover), canvas.getWidth() / 2,
						(canvas.getHeight() / 2), paint);
			}
        
        }
        private void pintarInterfaz(Canvas canvas, Paint paint){
			//Dibujamos las balas que nos quedan en esta minifase
			int numbalas = 3 - disparos.size();
			for (int i = 0; i < numbalas; i++) {
				canvas.drawBitmap(imagenBala, 9 + i * 10, 393, paint);

			}
			
			//	Dibujamos los patos acertados/fallados de esta ronda
			for (int i = 0; i < ducksFaileds.length; i++) {
				if (pintadoEspecialSiEstaMostrandoResultados()) {
					pintandoLosPatosDeLaInterfaz(canvas, i, 100 + i * 12,
							393, paint);
				}
			}
			//Mostramos la Ronda actual en la que se encuentra
			paint.setColor(0xFFCCF543);
			paint.setTextSize(11);
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText("R=" + getRound(), 5, 378, paint);
			
			//Mostramos la Puntuación actual
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(mContext.getResources().getString(
					R.string.juego_puntuacion), 275, 402, paint);
			canvas.drawText("" + getScore(), 280, 412, paint);
        }
        private void pintarDisparos(Canvas canvas, Paint paint){
			paint.setColor(Color.WHITE);
			for (int i = 0; i < disparos.size(); i++) {
				Disparo d = (Disparo) disparos.elementAt(i);
				if (d.visible) {
					canvas.drawLine(d.x - 5, d.y - 5, d.x + 5, d.y + 5,
							paint);
					canvas.drawLine(d.x - 5, d.y + 5, d.x + 5, d.y - 5,
							paint);
				}
			}
        }
        
        private void pintarBonificaciones(Canvas canvas, Paint paint){
			for (int i = 0; i < bonificaciones.size(); i++) {
				Bonificacion d = (Bonificacion) bonificaciones.elementAt(i);
				if (d.visible) {
					canvas.drawText(d.value + "", d.x, d.y, paint);
				}
			}
        }
        
        private boolean sePintaPerroAntesDeArbustos(){
        	return ((estadoJuego == JUEGO_ESTADO_INTRO && perro.getEstadoActual() == Perro.ESTADO_INTRO_SALTO_CAYENDO)
			|| estadoJuego == JUEGO_ESTADO_DERROTA
			|| estadoJuego == JUEGO_ESTADO_VICTORIA
			|| estadoJuego == JUEGO_ESTADO_PARTIDATERMINADA);
        }
        
        private boolean sePintaPerroDespuesDeArbustos(){
        	return estadoJuego == JUEGO_ESTADO_INTRO && perro.getEstadoActual() != Perro.ESTADO_INTRO_SALTO_CAYENDO;
        }

        
        
        /**
         * Establece el estado de Pausa cuando el usuario pulsa Menu, o se produce
         * un estado que interrumpa nuestra Activity
         */
        public void estableceEstadoPausa(){
        	if (estadoJuego!=JUEGO_ESTADO_PAUSA){
	        	//efectos_de_sonido.pausarSonido();
	        	efectos_de_musica.pausarSonido();
	        	estadoAnteriorJuego=estadoJuego;
	        	estadoJuego=JUEGO_ESTADO_PAUSA;
	        	tiempoPasadoEnPausa=System.currentTimeMillis();
        	}
        }
        /**
         * Reanuda la partida tras haberse pausado
         * 
         */
        public void vuelveAlJuegoTrasLaPausa(){
        	efectos_de_musica.reanudarSonido();
        	estadoJuego=estadoAnteriorJuego;
        	timeGame+=System.currentTimeMillis()-tiempoPasadoEnPausa;
        	if (perro!=null){
        		perro.actualizarTiempoEntreEstadosTrasLaPausa(System.currentTimeMillis()-tiempoPasadoEnPausa);
        	}
        	for (int i=0;i<enemys.size();i++){
        		Pato e=(Pato)enemys.elementAt(i);
        		e.actualizarTiempoEntreEstadosTrasLaPausa(System.currentTimeMillis()-tiempoPasadoEnPausa);
        	}
        }
        
        /**
         *	 Añade un efecto de parpadeo sobre los patos que se pintan sobre la interfaz
         *	 cuando termina la ronda actual
         *	
         */
        
        public boolean pintadoEspecialSiEstaMostrandoResultados(){
        	return ((estadoJuego!=JUEGO_ESTADO_MOSTRANDO_RESULTADO && estadoJuego!=JUEGO_ESTADO_PARTIDATERMINADA)|| System.currentTimeMillis()%200>100);
        }
        
        /**
         *	 Añade un efecto de parpadeo sobre los patos que se pintan sobre la interfaz
         *	 cuando termina la ronda actual
         *	
         */
        public void pintandoLosPatosDeLaInterfaz(Canvas canvas,int indicePato,int x,int y, Paint paint){
        	boolean esElPatoActual=false;
        	if (pintadoInterfazMientrasJuega()){
	            for (int i=0;i<enemys.size();i++){
	                
	            	Pato e=(Pato)enemys.elementAt(i);
	                if (e.getID()== indicePato){ // Si es el Pato Actual
	                	if (e.getEstadoActual()==Pato.STATE_MOVE){	
	                		//Si esta en movimiento, mostraremos el pato blanco con un efecto
	                		//de parpadeo
		                	if (System.currentTimeMillis()%500>250){
		                		canvas.drawBitmap(imagenInterfazPatoFallado, x,y, paint);
		                	}
	                	}else if (patoAcertado(e)){
	                		//Si el pato esta muerto, o esta cayendo, usamos el pato rojo
	                		canvas.drawBitmap(imagenInterfazPatoAcertado, x,y, paint);
	                	}else if (patoFallado(e)){
	                		//Si el pato esta huyendo, lo pintamos blanco.
		                	canvas.drawBitmap(imagenInterfazPatoFallado, x,y, paint);
	                	}
	                	esElPatoActual=true;
	                }
	            }
        	}
            if (!esElPatoActual){ 
            	// Si No Es el Pato Actual, accedemos al array donde guardamos el historial de los patos acertados
            	// en esta ronda.
            	canvas.drawBitmap(ducksFaileds[indicePato]?imagenInterfazPatoAcertado:imagenInterfazPatoFallado, 100+indicePato*12,393, paint);
            }

        }
        private boolean pintadoInterfazMientrasJuega(){
        	return estadoJuego==JUEGO_ESTADO_PLAY || estadoJuego==JUEGO_ESTADO_PAUSA;
        }
        private boolean patoAcertado(Pato pato){
        	return pato.getEstadoActual()==Pato.STATE_HITTED || pato.getEstadoActual()==Pato.STATE_CAYENDO || pato.getEstadoActual()==Pato.STATE_MUERTO;
        }
        private boolean patoFallado(Pato pato){
        	return pato.getEstadoActual()==Pato.STATE_HUYENDO || pato.getEstadoActual()==Pato.STATE_ESCAPO;
        }
        long timeGame=System.currentTimeMillis();
        long tiempoPasadoEnPausa;
        public long getTiempoTranscurrido(){
        	return System.currentTimeMillis() - timeGame;
        }
        ArrayList<Drawable> listDrawable;
        Bitmap imagenPato ;
        Bitmap imagenFondo ;
        Bitmap imagenPerro ;
        Bitmap imagenBala ;
        Bitmap imagenInterfazPatoAcertado ;
        Bitmap imagenInterfazPatoFallado ;
        Perro perro;
        int estadoJuego;
        int estadoAnteriorJuego;



        public static final int JUEGO_ESTADO_INICIO=0;
        public static final int JUEGO_ESTADO_NUEVA_RONDA=1;
        public static final int JUEGO_ESTADO_INTRO=2;
        public static final int JUEGO_ESTADO_RESET=3;
        public static final int JUEGO_ESTADO_PLAY=4;
        public static final int JUEGO_ESTADO_DERROTA=5;
        public static final int JUEGO_ESTADO_VICTORIA=6;
        public static final int JUEGO_ESTADO_CALCULANDO_RESULTADO=7;
        public static final int JUEGO_ESTADO_MOSTRANDO_RESULTADO=8;
        public static final int JUEGO_ESTADO_PERFECT=9;
        public static final int JUEGO_ESTADO_PAUSA=10;
        public static final int JUEGO_ESTADO_PARTIDATERMINADA=11;
        
        
        /**
         *	 Si pasa un tiempo minimo, o si se queda sin balas, los patos huyen
         */
        private void hacerHuirATodosLosPatos(){
      	  	for (int i=0;i<enemys.size();i++){
          		Pato e=(Pato)enemys.elementAt(i);
          		if (e.getEstadoActual()==Pato.STATE_MOVE){
          			e.iniciarCicloHuyendo();

          		}
      		}
        }
        ObjetoAnimable animacion_pajaro;

        /**
         *	 Una ronda constara de 10 minifases en el modo de juego de un pato, 
         *	 y de 5 minifases en el modo de juego dos patos. Entre minifases, habra que
         *	 reiniciar una serie de parametros necesarios, ademas de crear lo/s pato/s
         *	 necesarios. Tambien comprobaremos si se han completado todas las minifases,
         *	 y en ese caso cambiaremos a un estado donde mostraremos el resultado de la ronda.
         */
        private void resetvalues(){
        	if (currentDuck==8){
        		estadoJuego=JUEGO_ESTADO_CALCULANDO_RESULTADO;
        	}else{
        		enemys=new Vector();
        		disparos=new Vector();
        		bonificaciones=new Vector();
        		int index=0;
        		while (index<totalDePatosPorMiniFase){
        			Pato pato=new Pato(new ObjetoAnimable(imagenPato,Pato.ANIMATIONS,Pato.TIME_FRAMES,Pato.kSpriteSize),this,mContext);
        			pato.iniciarCicloVolando(indicePatos);

        			indicePatos++;
        			enemys.add(pato);
        			index++;
        		}
        		timeGame=System.currentTimeMillis();
        		estadoJuego=JUEGO_ESTADO_PLAY;
        		solicitarMusica(MUSICA_VOLAR);
        		currentDuck+=totalDePatosPorMiniFase;
        	}
        }

        private boolean ducksFaileds[];
        public int currentDuck=-totalDePatosPorMiniFase;
        
        /**
         *	Esta función se encargará de indicar a los patos que se procesen, y en caso de 
         *	que haya terminado la minifase, decidira si mostrará la indicación de victoria
         *	o derrota.
         */
        private void processDucks(){
        	for (int i=0;i<enemys.size();i++){
        		Pato e=(Pato)enemys.elementAt(i);
        		e.process();
        		if (patosEnPantalla()==0 && patosAcertados()==0){
        			estadoJuego=JUEGO_ESTADO_DERROTA;
        			perro.iniciarCicloDerrota();
        			efectos_de_musica.pararSonido(MUSICA_VOLAR);
        			solicitarSonido(SONIDO_RISA);
        			break;
        		}else{
        			if (patosAcertados()>0 && patosEnPantalla()==0){
        				perro.iniciarCicloVictoria(patosAcertados());
        				efectos_de_musica.pararSonido(MUSICA_VOLAR);
        				estadoJuego=JUEGO_ESTADO_VICTORIA;
        				solicitarSonido(SONIDO_VICTORIA);
        				break;
        			}
        		}
        	}
        }

        private int patosAcertados(){
        	int contador=0;
        	for (int i=0;i<enemys.size();i++){
        		Pato e=(Pato)enemys.elementAt(i);
        		if (e.getEstadoActual()==Pato.STATE_MUERTO){
        				contador++;
    			}
    		}
        	return contador;
        }
        private int patosEnPantalla(){
        	int contador=0;
        	for (int i=0;i<enemys.size();i++){
        		Pato e=(Pato)enemys.elementAt(i);
        		if (e.getEstadoActual()!=Pato.STATE_ESCAPO && e.getEstadoActual()!=Pato.STATE_MUERTO){
        				contador++;
    			}
    		}
        	return contador;
        }
        private int patosTodaviaVolando(){
        	int contador=0;
        	for (int i=0;i<enemys.size();i++){
        		Pato e=(Pato)enemys.elementAt(i);
        		if (e.getEstadoActual()==Pato.STATE_MOVE){
        				contador++;
    			}
    		}
        	return contador;
        }
        public int patosCayendo(){
        	int contador=0;
        	for (int i=0;i<enemys.size();i++){
        		Pato e=(Pato)enemys.elementAt(i);
        		if (e.getEstadoActual()==Pato.STATE_CAYENDO){
        				contador++;
    			}
    		}
        	return contador;
        }
        /**
         *	Esta función mantendra en pantalla durante 300 milisegundos un indicador
         *	de la ultima bonificación que se consiguio al eliminar el ultimo pato. 
         */
        private void procesarBonificaciones(){
          	for (int i=0;i<bonificaciones.size();i++){
          		Bonificacion e=(Bonificacion)bonificaciones.elementAt(i);
          		e.time+=GAP_PROCESS;
          		if (e.time>2000){
          			e.visible=false;
          		}
          	}
        }
        /**
         *	Este bloque de estructuras (atributos y metodos) nos permitiran saber en que
         *	ronda nos encontramos actualmente.
         */
        private int round;
        private int getRound(){
        	return round;
        }
        private void incRound(){
        	round++;
        }
        private void setFirstRound(){
        	round=0;
        }
        /**
         *	Este bloque de estructuras (atributos y metodos) nos permitiran saber cual
         *	es nuestra actual puntuación.
         */
        private int score;
        public int getScore(){
        	return score;
        }
        private void incScore(int value){
        	score+=value;
        }
        
        /**
         *	Este bloque de estructuras (atributos y metodos) nos permitiran reproducir sonidos
         *	en el momento que decidamos. Podremos pausar el sonido actual en caso de que el usuario
         *	entre en pausa.
         *
         *	En nuestro caso, dispondremos de varios sonidos asi que guardaremos un identificador
         *	en un array, por lo tanto a la hora de reproducir un sonido, tendremos que indicar
         *	cual es el sonido.
         *	
         */
        
        /**
        *	Array que contendra todos nuestros sonidos
        */
		//public int []Sounds={};
		
		//public int []Sounds={R.raw.inicio_ronda,R.raw.risa,R.raw.win,R.raw.disparo,R.raw.volando,R.raw.caida,R.raw.cayo,R.raw.wininterface,R.raw.winronda,R.raw.perfect};
		public int []Sounds_effects={R.raw.risa,R.raw.win,R.raw.disparo,R.raw.caida,R.raw.cayo,R.raw.wininterface};
		public int []Sonidos_de_musica={R.raw.inicio_ronda,R.raw.volando,R.raw.winronda,R.raw.perfect,R.raw.game_over};
		
		 /**
        *	Objeto encargado de reproducir los sonidos que solicitemos
        */
		Efectos_Sonido efectos_de_sonido;
		
		Sonido_Musica efectos_de_musica;
		
		/**
	    *	Identificadores que usaremos para solitiar el sonido adecuado al momento oportuno.
	    */
        
        public static final int SONIDO_RISA=0;
        public static final int SONIDO_VICTORIA=1;
        public static final int SONIDO_DISPARO=2;
        public static final int SONIDO_CAIDA=3;
        public static final int SONIDO_CAYO=4;
        public static final int SONIDO_WININTERFACE=5;
        
        public static final int MUSICA_INICIO=0;
        public static final int MUSICA_VOLAR=1;
        public static final int MUSICA_WINRONDA=2;
        public static final int MUSICA_WINPERFECT=3;
        public static final int MUSICA_PARTIDATERMINADA=4;
        //public static final int SONIDO_VOLAR=4;
        //public static final int SONIDO_INICIO=0;
        //public static final int SONIDO_WINRONDA=8;
        //public static final int SONIDO_WINPERFECT=9;
        
        
//        //public static final int SONIDO_INICIO=0;
//        public static final int SONIDO_RISA=1;
//        public static final int SONIDO_VICTORIA=2;
//        public static final int SONIDO_DISPARO=3;
//        //public static final int SONIDO_VOLAR=4;
//        public static final int SONIDO_CAIDA=5;
//        public static final int SONIDO_CAYO=6;
//        public static final int SONIDO_WININTERFACE=7;
//        //public static final int SONIDO_WINRONDA=8;
//        //public static final int SONIDO_WINPERFECT=9;
		/**
 	    *	Para todos el sonido actual que se este reproduciendo.
 	    */
        public void stopSound(){
        	efectos_de_musica.pausarSonido();
        	//efectos_de_sonido.pararSonido();
        }
        
		/**
  	    *	Reproduce el sonido solicitado. 
  	    */
        public int solicitarSonido(int s) {
        	int aux=-1;
			if (sonido) {
				//sonidos.playSonido(s,channel);
				aux=efectos_de_sonido.playSonido(s);
			}
			return aux;
		}
        public void solicitarMusica(int s) {
			if (sonido) {
				//sonidos.playSonido(s,channel);
				efectos_de_musica.playSonido(s);
			}
		}
        private int realPointX;
        private int realPointY;
        private static final int realPointSize=20;
        private void actualizapuntero(){
        	if (getModulo(punteroX,punteroY)>5){
        		realPointX+=punteroX/8;
            	realPointY-=punteroY/8;        		
        	}
        	if (realPointX>mCanvasWidth-(realPointSize>>1)){
        		realPointX=mCanvasWidth-(realPointSize>>1);
        	}
        	if (realPointY>mCanvasHeight-(realPointSize>>1)){
        		realPointY=mCanvasHeight-(realPointSize>>1);
        	}
        	if (realPointX<(realPointSize>>1)){
        		realPointX=(realPointSize>>1);
        	}
        	if (realPointY<(realPointSize>>1)){
        		realPointY=(realPointSize>>1);
        	}
        }
        private void paintPuntero(Canvas canvas, Paint p){
        	if (control==CONTROL_MOVE_MODE){
	//        	canvas.drawText(punteroX+":"+punteroY, 100, 100, p);
	//        	canvas.drawLine(mCanvasWidth/2, mCanvasHeight/2, (mCanvasWidth/2)+punteroX, (mCanvasHeight/2)+punteroY, p);
	        	if (getModulo(punteroX,punteroY)<15){
	        		p.setColor(0xFFFF0000);
	        	}else{
	        		p.setColor(0xFF00FF00);
	        	}
	        	
	        	Paint mipaint = new Paint();
	        	mipaint.setAntiAlias(true);
	        	mipaint.setStyle(Paint.Style.STROKE);
	        	mipaint.setStrokeWidth(3);
	        	mipaint.setColor(0xFF0000FF);
	        	//canvas.drawRect(realPointX, realPointY, realPointX+20, realPointY+20, p);
	        	RectF oval =new RectF(realPointX-(realPointSize>>1), realPointY-(realPointSize>>1), realPointX+(realPointSize>>1), realPointY+(realPointSize>>1));
	        	canvas.drawArc(oval, 0f, 360f, false, mipaint);
	        	mipaint.setStrokeWidth(2);
	        	mipaint.setColor(0x880000FF);
	        	oval =new RectF(realPointX-2, realPointY-2, realPointX+2, realPointY+2);
	        	canvas.drawArc(oval, 0f, 360f, false, mipaint);
        	}

        }
        private float getModulo(float x, float y){
        	return (float)Math.sqrt(x*x+y*y);
        }

        private void procesoJuego() {

        	procesarSolicitudesDeDisparo();
        	actualizapuntero();
        	if (efectos_de_musica!=null){
        		efectos_de_musica.prepareSounds();
        	}
        	switch (estadoJuego){
        	case JUEGO_ESTADO_PAUSA:
        		
        		break;
        	case JUEGO_ESTADO_INICIO:
            	if (imagenPato==null){
            		Resources resources = mContext.getResources();
            		efectos_de_sonido=new Efectos_Sonido(mContext);
            		efectos_de_sonido.init(Sounds_effects);
            		efectos_de_musica=new Sonido_Musica(Sonidos_de_musica,mContext);
            		imagenPato = BitmapFactory.decodeResource(resources, R.drawable.avatar);
            		imagenFondo = BitmapFactory.decodeResource(resources, R.drawable.fondo);
            		imagenPerro = BitmapFactory.decodeResource(resources, R.drawable.perro);
            		imagenBala = BitmapFactory.decodeResource(resources, R.drawable.bala);
            		imagenInterfazPatoAcertado = BitmapFactory.decodeResource(resources, R.drawable.duck);
            		imagenInterfazPatoFallado= BitmapFactory.decodeResource(resources, R.drawable.duckwhite);
            		//listDrawable.add(object)
            		perro = new Perro(imagenPerro,this);
            		solicitarMusica(MUSICA_INICIO);
            		setFirstRound();
            		ducksFaileds=new boolean[10];
                    estadoJuego=JUEGO_ESTADO_NUEVA_RONDA;
            	}
        		break;
        	case JUEGO_ESTADO_NUEVA_RONDA:
            	indicePatos=0;
        		perro.iniciarEstadoDeIntro();
        		estadoJuego=JUEGO_ESTADO_INTRO;
        		incRound();
        		ducksFaileds=new boolean[10];
            	currentDuck=-totalDePatosPorMiniFase;
        		break;
        	case JUEGO_ESTADO_MOSTRANDO_RESULTADO:
        		if (System.currentTimeMillis()-timeGame>5000){
        			timeGame=System.currentTimeMillis();
        			if (numeroDePatosAcertados()==10){
        				solicitarMusica(MUSICA_WINPERFECT);
        				estadoJuego=JUEGO_ESTADO_PERFECT;
        				incScore(30000);
        			}else{
        				estadoJuego=JUEGO_ESTADO_NUEVA_RONDA;
        			}
        		}
        		break;
        	case JUEGO_ESTADO_PERFECT:
        		if (System.currentTimeMillis()-timeGame>5000){

        				estadoJuego=JUEGO_ESTADO_NUEVA_RONDA;
        
        		}
        		break;
        	case JUEGO_ESTADO_INTRO:
        		if (perro.process()){
        			estadoJuego=JUEGO_ESTADO_RESET;
        		}
        		break;
        		
        	case JUEGO_ESTADO_RESET:
        		resetvalues();
        		break;

        	case JUEGO_ESTADO_DERROTA:
        		if (perro.process()){
        			resetvalues();
        			timeGame=System.currentTimeMillis();
        		}
        		procesDisparos(JuegoThread.GAP_PROCESS);
        		procesarBonificaciones();
        		break;
        	case JUEGO_ESTADO_VICTORIA:
        		procesDisparos(JuegoThread.GAP_PROCESS);
        		procesarBonificaciones();
        		if (perro.process()){
        			resetvalues();
					timeGame = System.currentTimeMillis();
				}
				break;
			case JUEGO_ESTADO_PLAY: {
				if (System.currentTimeMillis()-timeGame > 7000) {
					hacerHuirATodosLosPatos();
					timeGame = System.currentTimeMillis();
				}
				processDucks();
				procesDisparos(JuegoThread.GAP_PROCESS);
				procesarBonificaciones();
				break;
        	}
			case JUEGO_ESTADO_PARTIDATERMINADA:{
				perro.process();
				if (System.currentTimeMillis()-timeGame > 4000) {
					
					
					if (gameover==0){
						j.mHandler.sendEmptyMessage(0);
						System.out.println("Game over=1");
						gameover=1;
					}

				}
				break;
			}


        	case JUEGO_ESTADO_CALCULANDO_RESULTADO:
        		if (System.currentTimeMillis()-timeGame>500){
        			boolean cambios=false;
        			for (int i=0;i<ducksFaileds.length;i++){
        				if (!ducksFaileds[i]){
        					//cambios=true;
        					timeGame=System.currentTimeMillis();
        					if (llevarAlFinal(i)){
        						cambios=true;
        						solicitarSonido(SONIDO_WININTERFACE);
        					}
    						break;
        				}
        			}
        			if (!cambios){
        				if (numeroDePatosAcertados()>5){
        					solicitarMusica(MUSICA_WINRONDA);
        					estadoJuego=JUEGO_ESTADO_MOSTRANDO_RESULTADO;
        				}else{
        					solicitarMusica(MUSICA_PARTIDATERMINADA);
        					estadoJuego=JUEGO_ESTADO_PARTIDATERMINADA;
        					timeGame=System.currentTimeMillis();
        					perro.iniciarCicloDerrota();
        				}
        			}
        		}
        		break;

        	}

        	
        	
        }

        int gameover=0;
        /**
         * Esta función devuelve si el usuario ha hecho un Perfect en esta Ronda.
         * @return True si ha hecho perfect.
         */
		public int numeroDePatosAcertados() {
			int contador=0;
			for (int i = 0; i < ducksFaileds.length; i++) {
				if (ducksFaileds[i]) {
					contador++;
				}
			}
			return contador;
		}
		/**
		 * Durante la etapa en la que se calcula el resultado, va desplazando uno a uno
		 * los indicadores de la interfaz hasta que deja todos los objetivos fallados en el 
		 * lado derecho.
		 * @param array que contiene los indicadores de la interfaz
		 * @param index	actual indice que calculará si tiene que ser desplazado
		 * @return	True si ya estan todos los elementos colocados
		 */
		public boolean llevarAlFinal( int index) {
			int ultimaposicionlibre = ducksFaileds.length - 1;
			for (int i = ducksFaileds.length - 1; i >= 0; i--) {
				if (ducksFaileds[i]) {
					ultimaposicionlibre = i;
					break;
				}
			}
			if (index <= ultimaposicionlibre) {
				ducksFaileds[index] = true;
				ducksFaileds[ultimaposicionlibre] = false;
				System.out.println(index+" X "+ultimaposicionlibre);
				return true;
			}
			return false;

		}

		/**
		 * Cada vez que nuestra aplicación detecte un cambio en el tamaño de pantalla (solo
		 *  es posible al inicio de la aplicación, y cuando el usuario abre o cierra el teclado
		 *  QWERTY) actualizaremos nuestras dos variables que guardan el ancho y alto
		 *  de nuestro entorno de pintado. Además, añadiremos una restricción para que solo sea
		 *  al inicio de la aplicación, impidiendo asi poder jugar en modo horizontal.
		 * @param width  	Ancho al que se inicializará nuestra pantalla
		 * @param height	Alto al que se inicializara nuestra pantalla
		 */
        public void estableceTamanio(int width, int height) {
            synchronized (mSurfaceHolder) {
            	//Si no ha sido previamente inicializadas nuestras variables, las inicializamos ahora.
            	if (mCanvasWidth==-1){ 
	                mCanvasWidth = width;
	                mCanvasHeight = height;
            	}

            }
        }
    }

    /** Contexto de nuestra aplicación */
    public Context mContext;


    /** Thread de nuestra aplicación */
    private JuegoThread thread;
    
    /** Total de patos que saldran en una minifase */
    private static int totalDePatosPorMiniFase=-1;
    
    /**
     * Establece el numero de patos que tendra una minifase
     * @param total de patos
     */
    public static void setTotalDePatosPorMiniFase(int total){
    	totalDePatosPorMiniFase=total;
    }

    public JuegoView(Context context, AttributeSet attrs) {

        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // Aqui creamos el thread solo, lo iniciaremos en surfaceCreated()
        thread = new JuegoThread(holder, context);
    	// Establecemos este View para que coja el foco, y pueda interactuar el usuario con él.
        setFocusable(true);
        setFocusableInTouchMode(true);
        // Cargamos la preferencias del usuario
        readPreferences();

        
    }
    
    private boolean sonido;
    private boolean vibracion;
    private int control;
    public static final int CONTROL_TOUCH_MODE=0;
    public static final int CONTROL_MOVE_MODE=1;
    
    
    /** Cuando el usuario modifica sus preferencias, debemos actualizarlas en caliente durante la partida,
     * además de al inicio de esta. Lo que haremos es cargar las opciones de sonido, vibración, y el modo
     * de control que desea el jugador.*/
    public void readPreferences() {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		sonido = app_preferences.getBoolean("preference_sonido", false);
		vibracion = app_preferences.getBoolean("preference_vibracion", false);
		String valor_control = app_preferences.getString(
				"preference_controles", "no valor");

		if (valor_control.equals("tm")) {
			// Touch Mode
			control = CONTROL_TOUCH_MODE;
		} else {
			// Move Mode
			control = CONTROL_MOVE_MODE;
		}
	}

    public JuegoThread getThread() {
        return thread;
    }

    /**
     * Chequea si esta en el Modo Touch, y en ese caso, añade una solicitud de Disparo
     * 
     */
    public boolean onTouchEvent(MotionEvent event) {
    	if (control==CONTROL_TOUCH_MODE){
        	int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN){
            	thread.aniadirSolicitudDisparo((int)event.getX(),(int)event.getY());
            }
    	}

        return true;
    }
    /**
     * Chequea si esta en el Modo Move, y en ese caso, comprueba que haya sido pulsado
     * el trackball, y en ese caso añadira una SolicitudDeDisparo
     * 
     */
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
    	System.out.println("------Intentando añadir disparo");
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && control==CONTROL_MOVE_MODE){
            	thread.aniadirSolicitudDisparo(thread.realPointX,thread.realPointY);
            	System.out.println("Intentando añadir disparo");
            	return true;
            }else if(keyCode == KeyEvent.KEYCODE_BACK) {
            	return true;
            }
            return false;
    }
    
    /**
     * MOVE MODE
     * 
     * Move Mode es un modo de control del juego que nos permitira, gracias al acelerometro,
     * mediante el movimiento de nuestro telefono movil, poder desplazar la posición de nuestro
     * cursor.
     * 
     */
    

//    public final int MAXIMO_VALOR_EN_HORIZONTAL_RECIBIDO = 90 ;
//    public final int MAXIMO_VALOR_EN_VERTICAL_RECIBIDO = 90 ;
//    public final int PORCENTAJE_FINAL_EN_HORIZONTAL = 150 ;
//    public final int PORCENTAJE_FINAL_EN_VERTICAL = 150 ;
//    public int getAyudaEnHorizontal(int valor){
//    	return (valor*PORCENTAJE_FINAL_EN_HORIZONTAL)/100;
//    }
//    public int getAyudaEnVertical(int valor){
//    	return (valor*PORCENTAJE_FINAL_EN_VERTICAL)/100;
//    }
//    public int getTotalMovementHorizontal(){
//    	return thread.mCanvasWidth/2;
//    }
//    public int getTotalMovementVertical(){
//    	return thread.mCanvasHeight/2;
//    }
//    public int correctvalueX(float v){
//        return (int)((getTotalMovementHorizontal()*getAyudaEnHorizontal((int)v))/MAXIMO_VALOR_EN_HORIZONTAL_RECIBIDO);
//    }
//    public int correctvalueY(float v) {
//		return (int) ((getTotalMovementVertical() * getAyudaEnVertical((int) v)) / MAXIMO_VALOR_EN_VERTICAL_RECIBIDO);
//	}

	
	public float punteroX;
	public float punteroY;

	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			punteroX =values[2];
			punteroY =values[1];
		}
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		

	}

    /**
     * Llamada de retorno invocada cuando la dimension de nuestra superficie cambia
     * 
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    	thread.estableceTamanio(width, height);
    }

    /**
     * Llamada de retorno invocada cuando la Superficie es creada y lista para usarse
     * 
     */

    public void surfaceCreated(SurfaceHolder holder) {
        // Iniciamos aqui el Thread
        thread.setRunning(true);
        thread.start();
    }
    
    /**
     * Llamada de retorno invocada cuando la Superficie es cerrada
     * 
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    	// Indicamos al thread que se cierre, y esperamos hasta que se termine,
    	// sino esta podria recibir un evento y podria saltar un error
        boolean retry = true;
        //System.out.println("lalallaa");
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    
	/** Vector que contendra los Disparos realizados en una minifase.*/
    private Vector disparos=new Vector();
    
    /**
     * Clase que almacena los valores necesarios cuando un Disparo se lanza. Entre ellos su posición
     * x e y. Guardaremos un parametro visible que consultaremos para saber durante cuanto tiempo
     * tendremos que mostrar nuestro elemento.
     * 
     */
    public class Disparo{
    	int x,y;
    	int time;
    	boolean visible=true;
    	public Disparo(int x,int y){
    		this.x=x;
    		this.y=y;
    	}
    }
    
    /**
     *	Esta función mantendra en pantalla durante 300 milisegundos un indicador
     *	de donde se hizo el ultimo disparo. 
     */
    private void procesDisparos(int value){
      	for (int i=0;i<disparos.size();i++){
      		Disparo e=(Disparo)disparos.elementAt(i);
      		e.time+=value;
      		if (e.time>300){
      			e.visible=false;
      		}
      	}
    }
    
    public final int []VALORES_DE_BONIFICACIONES={1000,500,250};
    
	/** Vector que contendra las Bonificaciones obtenidas en una minifase.*/
    private Vector bonificaciones=new Vector();
    
    /**
     * Clase que almacena los valores necesarios cuando una Bonificación es obtenida tras haber
     * acertado sobre un pato. Guardaremos un parametro visible que consultaremos para saber durante cuanto tiempo
     * tendremos que mostrar el valor que ha obtenido por acertar sobre el pato. Este valor dependera
     * de la bala con cual se acierte: si es al primer intento, puntuará más.
     * 
     */
    public class Bonificacion{
    	int x,y;
    	int time;
    	int value;
    	boolean visible=true;
    	public Bonificacion(int x,int y,int value){
    		this.x=x;
    		this.y=y;
    		this.value=value;
    	}
    }

}
