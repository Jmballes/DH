
package com.paquete;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;


public class Sonido_Musica {
	int sonidos[];
	MediaPlayer player[];
	static final int STOP=0;
	static final int PLAYING=1;
	static final int PAUSED=2;
	int estado[];
	Context mContext;
	public Sonido_Musica(int[] sonidos,Context c){
		this.sonidos=sonidos;
		this.mContext=c;
		player= new MediaPlayer[sonidos.length];
		estado= new int[sonidos.length];
		for (int i=0;i<sonidos.length;i++){
			player[i] = MediaPlayer.create(mContext, sonidos[i]);
		}
	}
	public void pararSonido(int sonido){
		player[sonido].stop();
		try {
			player[sonido].prepare();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		estado[sonido]=STOP;
	}
	public void playSonido(int sonido){
		if (estado[sonido]==PLAYING){
			player[sonido].stop();
			try {
				player[sonido].prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			player[sonido].start();
			estado[sonido]=PLAYING;
			
		}else{
			if (estado[sonido]==STOP){
				player[sonido].start();
				estado[sonido]=PLAYING;
			}
		}

	}

	public void pausarSonido(){
		for (int i=0;i<sonidos.length;i++){
			if (estado[i]==PLAYING){
				estado[i]=PAUSED;
				player[i].pause();
			}
		}
	}
	
//	public void pararSonido(){
//		for (int i=0;i<sonidos.length;i++){
//			if (!player[i].isPlaying()){
//				player[i].stop();
//				
//			}
//		}
//
//	}
	
	public void prepareSounds(){
		for (int i=0;i<estado.length;i++){
			if (estado[i]==PLAYING){
				if (!player[i].isPlaying()){
					try {
						player[i].stop();
						//player[i].reset();
						player[i].prepare();

						estado[i]=STOP;
						System.out.println("preparado:"+i);
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void reanudarSonido(){
		for (int i=0;i<sonidos.length;i++){
			if (estado[i]==PAUSED){
				player[i].start();
				estado[i]=PLAYING;
				
			}
		}
	}
}