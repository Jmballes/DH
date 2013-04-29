package com.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.paquete.JuegoView.JuegoThread;
import com.paquete.Pato;

public class ObjetoAnimable {
	
	/**
	 * Actual animación que se esta mostrando.
	 */
	
	public int actualAnimacion;
	
	/**
	 * Tiempo transcurrido de la animación actual.
	 */
	public long tiempoAnimacion;
	
	
	/**
	 * 	Si una animación es ciclica, a pesar de que el tiempo transcurrido haya superado el total de la animación,
	 *  se reproducirá desde el inicio. 
	 */
	boolean ciclica;
	
	/**
	 * 	Indica si se tiene que hacer un espejado horizontal.
	 */
	boolean espejadoHorizontal;
	
	/**
	 * Indica el numero de animaciones que tiene, además de los sprites a los que corresponde
	 */
	public final byte[] ANIMACIONES;
	
	/**
	 * Tamaño de un sprite
	 */
	public final int TAMANIO_SPRITE;
	
	/**
	 * Tiempo que dura un Frame
	 */
	public final int TIEMPO_DURACION_SPRITES;
	
	/**
	 * Bitmap que contendra todos nuestros frames.
	 */
	public final Bitmap sprite;
	
	/**
	 * Constructor que nos permitira iniciar varios atributos.
	 */
	public ObjetoAnimable(Bitmap sprite,byte[] anim,int time,int size){
		this.sprite=sprite;
		this.ANIMACIONES=anim;
		this.TIEMPO_DURACION_SPRITES=time;
		this.TAMANIO_SPRITE=size;
	}
	
	/**
	 * Funcion que actualiza el tiempo actual de la animación
	 */
	public void actualizarTiempo(){
		//tiempoAnimacion+=JuegoThread.GAP_PROCESS;
	}
	/**
	 * Devuelve el tiempo que ha transcurrido en esta animación.
	 * @return Tiempo actual
	 */
	public long getTiempoActual(){
		return (System.currentTimeMillis()-tiempoAnimacion);
	}

	/**
	 * Devuelve el tiempo que dura la animación actual.
	 * @return	Tiempo que dura la animación actual
	 */
	private long  getTiempoTotal(){
		return ANIMACIONES[actualAnimacion*2+1]*TIEMPO_DURACION_SPRITES;
	}
	
	/**
	 * Establece si la animación se imprimira con efecto de espejado horizontal.
	 * @param value True si la animación tendra espejado horizontal
	 */
	public void setEspejadoHorizontal(boolean value){
		espejadoHorizontal=value;
	}
	
	/**
	 * Calcula el sprite que debe pintar de una animación.
	 * @param ciclica Establece si es ciclica la animación
	 * @return Devuelve el sprite a pintar.
	 */
	public final int getSpriteAPintar(){

	    long calcularTiempoActualLimpio=getTiempoActual();
	    //System.out.println("tiempo:"+calcularTiempoActualLimpio);
	    if (ciclica){
	    	calcularTiempoActualLimpio = (calcularTiempoActualLimpio % getTiempoTotal());
	    }else if (calcularTiempoActualLimpio >= getTiempoTotal()){

	      calcularTiempoActualLimpio = getTiempoTotal()-1;
	    }
	    int sprite = (int)(calcularTiempoActualLimpio/TIEMPO_DURACION_SPRITES);
	    
	    return ANIMACIONES[actualAnimacion*2]+sprite;
	} 
	
	/**
	 * Pinta una animación.
	 * @param canvas	Canvas sobre el que se pintará la animación
	 * @param x	Posición en el eje horizontal
	 * @param y	Posición en el eje vertical
	 * @param paint	Estilo con el que se pintara.
	 */
	
	public void pintarAnimacion(Canvas canvas,int x,int y, Paint paint) {
		int sprite=getSpriteAPintar();
		drawSprite(canvas,x,y, sprite, paint);
	}
	
	
	  
	  
	/**
	 * Pinta un sprite concreto.
	 * @param canvas	Canvas sobre el que se pintará la animación
	 * @param x	Posición en el eje horizontal
	 * @param y	Posición en el eje vertical
	 * @param indexsprite Sprite a pintar
	 * @param paint	Estilo con el que se pintara.
	 */
	public void drawSprite(Canvas canvas,int x,int y,int indexsprite, Paint paint){
		RectF sprite_destination = new RectF(0, 0, TAMANIO_SPRITE,TAMANIO_SPRITE);
        
        
        Rect spriteorigen= new Rect(0,indexsprite*TAMANIO_SPRITE,TAMANIO_SPRITE,indexsprite*TAMANIO_SPRITE+TAMANIO_SPRITE);
    	
        if (espejadoHorizontal){
            canvas.save();
            canvas.scale(-1.0f, 1.0f);
        	canvas.translate(-x-Pato.kSpriteSize, y);
        	canvas.drawBitmap(sprite, spriteorigen, sprite_destination, paint);
            canvas.restore();
        }else{
        	sprite_destination.offset(x, y);
        	canvas.drawBitmap(sprite, spriteorigen, sprite_destination, paint);
        }

	}
	
	/**
	 * Inicia una animación en concreto.
	 * @param animacionAPintar Establece la animación actual
	 * @param ciclica	Establece si será ciclica.
	 */
	public void initAnimation(int animacionAPintar,boolean ciclica){
		actualAnimacion=animacionAPintar;
		if (!ciclica){
		tiempoAnimacion=System.currentTimeMillis();
		}
		this.ciclica=ciclica;
	}

}
