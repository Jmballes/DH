package com.paquete;

public abstract class Personaje {
	private float z;
	/**
	 * Posición en el eje horizontal de nuestro personaje
	 */
	private float x;
	/**
	 * Posición en el eje vertical de nuestro personaje
	 */
	private float y;
	
	/**
	 * Establece la posición en el eje horizontal de nuestro personaje
	 * @return X
	 */
	public void setX(float x){
	  this.x=x;
	}
	/**
	 * Establece la posición en el eje vertical de nuestro personaje
	 * @return Y
	 */
	public void setY(float y){
	  this.y=y;
	}
	/**
	 * Devuelve la posición en el eje horizontal de nuestro personaje
	 * @return X
	 */
	public float getX(){
	  return x;
	}
	/**
	 * Devuelve la posición en el eje vertical de nuestro personaje
	 * @return Y
	 */
	public float getY(){
	    return y;
	}
	
	/**
	 * Velocidad en el eje horizontal de nuestro personaje
	 */
	private float vx;
	/**
	 * Velocidad en el eje vertical de nuestro personaje
	 */
	private float vy;
	
	
	
	/**
	 * Establece la velocidad en el eje horizontal de nuestro personaje
	 * @return X
	 */
	public void setVx(float vx){
	  this.vx=vx;
	}
	/**
	 * Establece la velocidad en el eje vertical de nuestro personaje
	 * @return Y
	 */
	public void setVy(float vy){
	  this.vy=vy;
	}
	/**
	 * Devuelve la velocidad en el eje horizontal de nuestro personaje
	 * @return X
	 */
	public float getVx(){
	  return vx;
	}
	/**
	 * Devuelve la velocidad en el eje vertical de nuestro personaje
	 * @return Y
	 */
	public float getVy(){
	    return vy;
	}
	
	
	
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Destino en el eje horizontal de nuestro personaje
	 */
	private float destinyx;
	/**
	 * Destino en el eje vertical de nuestro personaje
	 */
	private float destinyy;
	
	/**
	 * Establece el destino en el eje horizontal de nuestro personaje
	 * @return X
	 */
	public void setDestinyX(float destinyx){
	  this.destinyx=destinyx;
	}
	/**
	 * Establece el destino en el eje vertical de nuestro personaje
	 * @return Y
	 */
	public void setDestinyY(float destinyy){
	  this.destinyy=destinyy;
	}
	/**
	 * Devuelve el destino en el eje horizontal de nuestro personaje
	 * @return X
	 */
	public float getDestinyX(){
	  return destinyx;
	}
	/**
	 * Devuelve el destino en el eje vertical de nuestro personaje
	 * @return Y
	 */
	public float getDestinyY(){
	    return destinyy;
	}
	
	/**
	 * Función que movera nuestro personaje en los dos ejes hacia su destino.
	 */
	public void moverPersonaje(){
		setX(nearValue(getX(),getDestinyX(),getVx()));
		setY(nearValue(getY(),getDestinyY(),getVy()));
	}
	/**
	 * Averigua si nuestro personaje ha llegado a su destino
	 * @return Ha llegado a su destino
	 */
	public boolean llegoAlDestino(){
		return getX()==getDestinyX() && getY()==getDestinyY();
	}
	
	/**
	 * Función que se encarga de acercar un valor hacia otro.
	 * @param origen	Valor actual
	 * @param destino	Destino final
	 * @param inc	Incremento hacia el destino
	 * @return	Valor aproximado.
	 */
	private float nearValue(float origen, float destino,float inc){
		float aux;
	
		if (origen>destino){
			aux=origen-inc;
			if (aux<destino){
				aux=destino;
			}
		}else{
			aux=origen+inc;
			if (aux>destino){
				aux=destino;
			}
		}
		return aux;
	}
	
	/**
	 * Estado de nuestro personaje
	 */
	private byte estado;
	
	/**
	 * Establece el nuevo estado de nuestro personaje
	 * @param nuevoEstado
	 */
	public void setEstado(byte nuevoEstado){
		this.estado=nuevoEstado;
		reiniciarTiempoEntreEstados();
	}
	
	/**
	 * Devuelve el estado de nuestro personaje
	 * @return estado
	 */
	public byte getEstadoActual(){
	    return estado;
	}
	
	/**
	 * Atributo que usaremos para saber cuanto tiempo pasa entre un estado y otro.
	 */
	private long tiempoEntreEstados;
	
	/**
	 * Devuelve el tiempo pasado entre estados
	 * @return	tiempo
	 */
	public long getTiempoEntreEstados(){
		return System.currentTimeMillis()-tiempoEntreEstados;
	}
	

//	/**
//	 * Actualiza el tiempo actual
//	 * @param incremento
//	 */
//	public void actualizarTiempoEntreEstados(long value){
//		tiempoEntreEstados+=value;
//	}
	/**
	 * Reinicia el tiempo del personaje
	 */
	public void reiniciarTiempoEntreEstados(){
		tiempoEntreEstados=System.currentTimeMillis();
	}
	public void actualizarTiempoEntreEstadosTrasLaPausa(long incremento){
		tiempoEntreEstados+=incremento;
	}
}
