package com.paquete;

import com.draw.ObjetoAnimable;
import com.paquete.JuegoView.JuegoThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Perro extends Personaje{

	
	


	/**
	 * Inicia nuestro ciclo de Intro
	 */
	public void iniciarEstadoDeIntro(){
		//	Inicia los puntos a los que se dirigirá nuestro personaje
		posicionesDuranteIntro=new int[4];
		posicionesDuranteIntro[0]=(juegoThread.mCanvasWidth/2)-(kSpriteSize/2)-60;
		posicionesDuranteIntro[1]=(juegoThread.mCanvasWidth/2)-(kSpriteSize/2)-40;
		posicionesDuranteIntro[2]=(juegoThread.mCanvasWidth/2)-(kSpriteSize/2)-20;
		posicionesDuranteIntro[3]=(juegoThread.mCanvasWidth/2)-(kSpriteSize/2)-0;
		
		//Se inicia desde el punto 0
		actualPosicionEnIntro=0;
		
		//Iniciamos los valores
		setX(posicionesDuranteIntro[actualPosicionEnIntro]);
		setDestinyX(posicionesDuranteIntro[actualPosicionEnIntro+1]);
		setVx(0.5f);
		//setVy(2f);
		setY(Pato.LIMIT_Y);
		setEstado(ESTADO_INTRO_MOVIENDOSE);

		anim.initAnimation(0, true);

	}
	
	/**
	 * Inicia Ciclo de Victoria
	 */
	public void iniciarCicloVictoria(int patos){
		setEstado(Perro.ESTADO_VICTORIA_APARECIENDO_DESAPARECIENDO);
		setVy(1.3f);
		setY(POSICION_VERTICAL_RIENDO_OCULTO);
		setDestinyY(Perro.POSICION_VERTICAL_RIENDO_VISIBLE);
		anim.initAnimation(4+patos, true);
	}
	
	/**
	 * Inicia diclo de Derrota
	 */
	public void iniciarCicloDerrota(){
		setEstado(Perro.ESTADO_RISA_APARECIENDO_DESAPARECIENDO);
		setVy(1.3f);
		setY(POSICION_VERTICAL_RIENDO_OCULTO);
		setDestinyY(Perro.POSICION_VERTICAL_RIENDO_VISIBLE);
		anim.initAnimation(4, true);
	}
	
	public static final int kSpriteSize = 56;
	


	

	public final static byte ESTADO_INTRO_MOVIENDOSE=1;
	public final static byte ESTADO_INTRO_OLFATEAR=2;
	public final static byte ESTADO_INTRO_SORPRESA=3;
	public final static byte ESTADO_INTRO_SALTO_SUBIENDO=4;
	public final static byte ESTADO_INTRO_SALTO_CAYENDO=5;
	public final static byte ESTADO_RISA_APARECIENDO_DESAPARECIENDO=6;
	public final static byte ESTADO_RISA_VISIBLE=7;
	public final static byte ESTADO_VICTORIA_APARECIENDO_DESAPARECIENDO=8;
	public final static byte ESTADO_VICTORIA_VISIBLE=9;

	public final static int TIME_FRAMES=300;
	public final static byte[] ANIMATIONS={0,3,
										3,2,
										5,1,
										6,2,
										8,2,
										10,1,
										11,1
										   
	};
	
	/**
	 * Referencia al Thread de nuestro juego
	 */
	JuegoThread juegoThread;
	
	/** Referencia a un objeto de tipo Animacion que nos permitira manejar las
	 * animaciones de nuestro personaje.
	 */
	ObjetoAnimable anim;
	
	/**
	 * Constructor para inicializar nustro personaje.
	 * @param bitmap	Bitmap que contendra los graficos de nuestro personaje
	 * @param juegoThread	Referencia al Thread de nuestro juego
	 */
	public Perro(Bitmap bitmap,JuegoThread juegoThread){
		this.juegoThread=juegoThread;
		anim= new ObjetoAnimable(bitmap,ANIMATIONS,TIME_FRAMES,kSpriteSize);
	}

	

	
	/**
	 * Posición en el eje vertical de nuestro personaje durante la Intro
	 */
	public static final int POSICION_VERTICAL_DURANTE_LA_INTRO=308;
	/**
	 * Posición en el eje vertical de nuestro personaje de donde aparece cuando
	 * sale riendose, y a la que se dirige cuando desaparece.
	 */
	public static final int POSICION_VERTICAL_RIENDO_OCULTO=308;
	/**
	 * Posición en el eje vertical de nuestro personaje cuando aparece riendose.
	 */
	public static final int POSICION_VERTICAL_RIENDO_VISIBLE=255;
	
	/**
	 * Función que pintara nuestro personaje en las coordenadas previamente procesadas
	 * @param canvas	Canvas sobre el que se pintará nuestro personaje.
	 * @param paint	Estilo con el que se pintara.
	 */
	public void paint(Canvas canvas,Paint paint){
		if (anim!=null){
			anim.pintarAnimacion(canvas,(int)getX(),(int)getY(),paint);
		}
	}


	/**
	 * Posiciones por las que se movera nuestro personaje durante la Intro
	 */
	private int []posicionesDuranteIntro;
	/**
	 * Actual punto al que se dirige nuestro personaje durante la Intro
	 */
	private int actualPosicionEnIntro;
	
	

	
	
	/**
	 * Función encargada de procesar nuestro personaje. Sus estados estan automatizados y dicha función devolverá
	 * true, cuando su ciclo haya terminado. Podriamos considerar tres ciclos de estados para nuestro personaje:
	 * <p><b>Ciclo de Intro:</b> ESTADO_INTRO_INICIO, ESTADO_INTRO_MOVIENDOSE, 
	 *		ESTADO_INTRO_OLFATEAR, ESTADO_INTRO_SORPRESA, ESTADO_INTRO_SALTO_SUBIENDO, ESTADO_INTRO_SALTO_CAYENDO
	 * 
	 * <p><b>Ciclo de Risa:</b> ESTADO_RISA_APARECIENDO_DESAPARECIENDO, ESTADO_RISA_VISIBLE.
	 * <p><b>Ciclo de Victoria:</b> ESTADO_VICTORIA_APARECIENDO_DESAPARECIENDO, ESTADO_VICTORIA_VISIBLE.
	 * @return true si ha terminado su ciclo de estados.
	 */
	public boolean process(){
		anim.actualizarTiempo();
		//actualizarTiempoEntreEstados(JuegoThread.GAP_PROCESS);

		switch (getEstadoActual()) {

			case ESTADO_INTRO_MOVIENDOSE:{
				// 	En este estado nuestro personaje se movera a traves de unos puntos. Entre punto
				//	y punto nuestro personaje hara una animación de olfateo.
				moverPersonaje();	
				
				//Si ha llegado a su destino
				if (getX()==getDestinyX()){
					//Preguntamos si es el penultimo punto, ya que el ultimo lo guardaremos para que nuestro
					//	personaje durante el salto avance un poco.
					if (actualPosicionEnIntro+2>=posicionesDuranteIntro.length){
						//	Si lo es, cambiamos a un estado de sorpresa, que mostrara su animación.
						anim.initAnimation(2, false);
						setEstado(ESTADO_INTRO_SORPRESA);
						setDestinyY(Pato.LIMIT_Y-60);
					}else{
						//	Si no es el ultimo punto, cambiamos a un estado en el que nuestro personaje
						//	mostrara una animación en la que olfeatea el suelo. Tiempo más tarde, volverá
						//	a este estado para completar todos los puntos.
						anim.initAnimation(1, true);
						setEstado(ESTADO_INTRO_OLFATEAR);

						actualPosicionEnIntro++;
						setX(posicionesDuranteIntro[actualPosicionEnIntro]);
						setDestinyX(posicionesDuranteIntro[actualPosicionEnIntro+1]);
					}
				}
			break;
			}
			case ESTADO_INTRO_OLFATEAR:{
				//En este estado nuestro personaje olfateará el suelo, y volvera al estado anterior.
				if (getTiempoEntreEstados()>1000){
					anim.initAnimation(0, true);
					setEstado(ESTADO_INTRO_MOVIENDOSE);
					

				}
			break;
			}
			case ESTADO_INTRO_SORPRESA:{
				//	En este estado simplemente mostraremos una animación en la que nuestro personaje
				//	aparece sorprendido. Cuando pase un segundo, lo cambiaremos para que inicie
				//	el salto.
				if (getTiempoEntreEstados()>1000){
					anim.initAnimation(3, false);
					setEstado(ESTADO_INTRO_SALTO_SUBIENDO);
					setVy(2f);
					setDestinyY(POSICION_VERTICAL_RIENDO_VISIBLE);
				}
			break;
			}
			case ESTADO_INTRO_SALTO_SUBIENDO:{
				//	En este estado nuestro personaje iniciara su salto, ademas de moverse en el eje vertical,
				//	tambien lo hace un poco en el horizontal, a diferencia del estado siguiente que sera cuando
				//	esta cayendo.
				moverPersonaje();	
				if (getX()==getDestinyX() && getY()==getDestinyY()){
					setEstado(ESTADO_INTRO_SALTO_CAYENDO);
					setDestinyY(POSICION_VERTICAL_DURANTE_LA_INTRO);

				}
			break;
			}
			case ESTADO_INTRO_SALTO_CAYENDO:{
				//	En este estado nuestro personaje hara la parte en la que cae en el salto. Por tanto
				// 	se puede dar por concluido este ciclo de Intro.
				moverPersonaje();	
				if (getX()==getDestinyX() && getY()==getDestinyY()){
					return true;
				}
			break;
			}
			case ESTADO_RISA_APARECIENDO_DESAPARECIENDO:{
				//	En este estado nuestro personaje aparecera y desaparecera en el ciclo en el que pierde
				//	una minifase. Cuando aparezca por completo, cambiara a un estado visible, en el que se
				//	mostrará por encima del cesped durante un segundo, y volverá a este estado para desaparecer.
				//	Cuando desaparezca por completo, podemos dar por terminado este Ciclo.
				moverPersonaje();
				if (getY()==getDestinyY()){
					if (getDestinyY()==POSICION_VERTICAL_RIENDO_VISIBLE){
						setDestinyY(POSICION_VERTICAL_RIENDO_OCULTO);
						setEstado(ESTADO_RISA_VISIBLE);

					}else{
						return true;
					}
				}
			break;
			}
			case ESTADO_RISA_VISIBLE:{
				//	Estado en el que se mostrará a nuestro personaje riendose, y al cabo de un segundo
				//	volvera al estado anterior en el que desaparecera.
				if (getTiempoEntreEstados()>(juegoThread.estadoJuego==juegoThread.JUEGO_ESTADO_PARTIDATERMINADA?3000:300)){
					setEstado(ESTADO_RISA_APARECIENDO_DESAPARECIENDO);
				}
			break;
			}
			case ESTADO_VICTORIA_APARECIENDO_DESAPARECIENDO:{
				//	En este estado nuestro personaje aparecera y desaparecera en el ciclo en el que gana
				//	una minifase. Cuando aparezca por completo, cambiara a un estado visible, en el que se
				//	mostrará por encima del cesped durante un segundo, y volverá a este estado para desaparecer.
				//	Cuando desaparezca por completo, podemos dar por terminado este Ciclo.
				moverPersonaje();	
				if (getY()==getDestinyY()){
					if (getDestinyY()==POSICION_VERTICAL_RIENDO_VISIBLE){
						setDestinyY(POSICION_VERTICAL_RIENDO_OCULTO);
						setEstado(ESTADO_VICTORIA_VISIBLE);
					}else{
						return true;
					}
				}
			break;
			}
			case ESTADO_VICTORIA_VISIBLE:{
				// 	Estado en el que se mostrará a nuestro personaje con uno/dos pato/s en su mano
				// 	y al cabo de un segundo	volvera al estado anterior en el que desaparecera.
				
				if (getTiempoEntreEstados()>300){
					setEstado(ESTADO_VICTORIA_APARECIENDO_DESAPARECIENDO);

				}
			break;
			}

			
		}
		return false;
	}

}
