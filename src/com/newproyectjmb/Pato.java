package com.newproyectjmb;

import com.draw.ObjetoAnimable;
import com.newproyectjmb.JuegoView.JuegoThread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;

public class Pato extends Personaje{

	private int id;
	public static final int kSpriteSize = 35;
	

	  
	public final static float VELOCIDAD_CUANDO_HUYE=5;
	public final static float VELOCIDAD_CUANDO_CAE_DERRIBADO=2f;
	
	public final static byte STATE_MOVE=0;
	public final static byte STATE_HITTED=1;
	public final static byte STATE_CAYENDO=2;
	public final static byte STATE_MUERTO=3;
	public final static byte STATE_HUYENDO=4;
	public final static byte STATE_ESCAPO=5;
	
	public final static int TIME_FRAMES=100;
	public final static byte[] ANIMATIONS={0,3,
										3,3,
										9,1,
										10,1,
										6,3
										   
	};
	public final static int ANIM_MOVE_HORIZONTAL=0;
	public final static int ANIM_MOVE_DIAGONAL=1;
	public final static int ANIM_MOVE_GOLPEADO=2;
	public final static int ANIM_MOVE_CAYENDO=3;
	public final static int ANIM_MOVE_HUYENDO=4;
	JuegoThread juegoThread;
	ObjetoAnimable anim;
	public Pato(ObjetoAnimable anim,JuegoThread juegoThread,Context mContext){
		this.juegoThread=juegoThread;
		this.anim=anim;

	}
	/**
	 * Inicia Ciclo de Vuelo en el que el pato se movera de un punto a otro 
	 * @param id Identificador del pato
	 */
	public void iniciarCicloVolando(int id){
		//Asignamos un identificador a nuestro personaje
		this.id=id;
		//Asinamos a nuestro personaje al estado de movimiento
		setEstado(STATE_MOVE);
		//Asignamos como posición en el eje horizontal el centro de la pantalla
		setX(juegoThread.mCanvasWidth/2);
		//Y como posición en el eje vertical el limite donde se encuentra el cesped
		setY(LIMIT_Y);
		//A continuación, calcularemos el destino al que se dirigirá nuestro personaje.
		calcularNuevoDestino();
	}
	/**
	 * Inicia el ciclo en el que ha sido alcanzado por una bala.
	 */
	public void iniciarCicloDisparado(){
		setEstado(STATE_HITTED);
		anim.initAnimation(ANIM_MOVE_GOLPEADO,false);
		reiniciarTiempoEntreEstados();
	}
	/**
	 * Inicia el ciclo en el que empieza a huir el pato.
	 */
	public void iniciarCicloHuyendo(){
		setEstado(STATE_HUYENDO);
		anim.initAnimation(ANIM_MOVE_HUYENDO, true);
		setDestinyX(getX());
 		setDestinyY(-getH());
 		setVy(VELOCIDAD_CUANDO_HUYE);
	}

	
	public static final int LIMIT_Y=308;

	public static final int DISTANCIA_HACIA_SIGUIENTE_PUNTO=100;
	/**
	 * Calcula el punto de destino del Pato. Cada vez que nuestro personaje llegue a su destino, habra que recalcular dicho valor
	 * El procedimiento sera: calcular un angulo de forma aletoria y con una distancia dada y a partir de la posición actual,
	 * se calcula el punto de destino.
	 */
	private void calcularNuevoDestino(){
		do{
			calcularDestinoAleatorio();
		}while (esElDestinoNoSeaValido());

		setVx(Math.abs((getDestinyX()-getX())/50));
		setVy(Math.abs((getDestinyY()-getY())/50));
		iniciarAnimacionVolando();
	}
	
	private void calcularDestinoAleatorio(){
		//Calculamos un angulo aleatorio
		double angle=Math.random()*Math.PI*2;
		
		//Calculamos la componente en el Eje X con el angulo y el modulo precalculado
		double vectorX=Math.cos(angle)*DISTANCIA_HACIA_SIGUIENTE_PUNTO;
		//Calculamos la componente en el Eje Y con el angulo y el modulo precalculado
		double vectorY=Math.sin(angle)*DISTANCIA_HACIA_SIGUIENTE_PUNTO;
		//Y añadimos a la posición actual el vector calculado
		setDestinyX((int)(getX()+vectorX));
		setDestinyY((int)(getY()+vectorY));

	}
	private boolean esElDestinoNoSeaValido(){
		return (getDistance(getX(),getY(),getDestinyX(),getDestinyY())<100 ||
		getDestinyX()<0 || getDestinyY()<0 || getDestinyX()>juegoThread.mCanvasWidth-getW() || getDestinyY()>LIMIT_Y-getW());
	}
	/**
	 * Calcula la distancia entre dos puntos.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	int getDistance(float x1,float y1,float x2,float y2){
		return (int)Math.sqrt(Math.pow(x1-x2, 2)*Math.pow(y1-y2, 2));
		
	}
	/**
	 * Prepara la animación de vuelo de nuestro personaje
	 */
	void iniciarAnimacionVolando(){
		//Calcula que tipo de animación debe de usar, si la horizontal, o diagonal
		anim.initAnimation(getDestinyX()-getX()>DISTANCIA_HACIA_SIGUIENTE_PUNTO*2/3?ANIM_MOVE_HORIZONTAL:ANIM_MOVE_DIAGONAL,true);
		//Calcula el sentido de la animación y por tanto si tiene que hacer un espejado de la animación.
		anim.setEspejadoHorizontal(getDestinyX()<getX());
	}

	/**
	 * Función encargada de pintar a nuestro personaje
	 * @param canvas
	 * @param paint
	 */
	public void paint(Canvas canvas,Paint paint){
		
	    if (anim != null) {
	    	//Si esta nuestro personaje esta cayendo porque ha sido abatido
	    	if (getEstadoActual()==STATE_CAYENDO){
	    		//Haremos un efecto intermitente de espejado, igual que en el juego original.
		    	if (getTiempoEntreEstados()%200>100){
					anim.setEspejadoHorizontal(true);
				}else{
					anim.setEspejadoHorizontal(false);
				}
	    	}
	    	anim.pintarAnimacion(canvas,(int)getX(),(int)getY(),paint);
	    	//long sprite=(anim.getTiempoActual());
	    	//System.out.println("pato-->"+sprite);
	    }


	}
	/**
	 * Devuelve el identificador del personaje
	 * @return identificador
	 */
	public int getID(){
		return id;
	}

	/** Devuelve la altura de nuestro personaje
	 * 
	 * @return Altura
	 */
	public int  getH(){
	    return kSpriteSize;
	}

	/** Devuelve el ancho de nuestro personaje
	 * 
	 * @return Ancho
	 */
	public int  getW(){
	    return kSpriteSize;
	}

	public int ultimosonido;
	public void process(){
		//Actualizamos el tiempo de la animación
		anim.actualizarTiempo();
		//Actualizamos el tiempo 
		//actualizarTiempoEntreEstados(JuegoThread.GAP_PROCESS);
		switch (getEstadoActual()) {
			case STATE_MOVE:{
				//Actualizamos la posición de nuestro personaje
				moverPersonaje();
				//Si ha llegado a su destino
				if (llegoAlDestino()){
					//Calculamos nuevo destino
		        	calcularNuevoDestino();
				}
			break;
			}
			case STATE_HUYENDO:{
				//Actualizamos la posición de nuestro personaje
				moverPersonaje();
				//Si ha llegado a su destino
				if (llegoAlDestino()){
					//Ha escapado
					setEstado(STATE_ESCAPO);
				}
			break;
			}
			case STATE_HITTED:{
				//Si pasa un segundo desde que es disparado
				if (getTiempoEntreEstados()>500){
					//Fijamos como destino el suelo
					setDestinyY(LIMIT_Y);
					setDestinyX(getX());
					//A una velocidad
					setVy(VELOCIDAD_CUANDO_CAE_DERRIBADO);
					setVx(0);
					//Con cierta animacion
					anim.initAnimation(ANIM_MOVE_CAYENDO,true);
					//Y cambiamos al estado cayendo
					setEstado(STATE_CAYENDO);
					ultimosonido=juegoThread.solicitarSonido(juegoThread.SONIDO_CAIDA);

				}
			break;
			}
		    case STATE_CAYENDO:{
				//Actualizamos la posición de nuestro personaje
				moverPersonaje();
				//Si ha llegado a su destino
				if (llegoAlDestino()){
					//Cambiamos al estado muerto
					setEstado(STATE_MUERTO);
					juegoThread.solicitarSonido(juegoThread.SONIDO_CAYO);
					juegoThread.efectos_de_sonido.pausarSonido(ultimosonido);
				}
		      break;
			}
		}

	}

}
