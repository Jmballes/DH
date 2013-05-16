package com.newproyectjmb;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class Efectos_Sonido { 
	int sonidos[];
	
    private boolean enabled = true; 
    private Context context; 
    private SoundPool soundPool; 

    private HashMap<Integer, Integer> soundPoolMap; 
    public Efectos_Sonido(Context context) { 
            this.context = context; 
    } 

    public void init(int[] sonidos) {
            if (enabled) { 
                    release(); 
                    soundPool = new SoundPool(sonidos.length*2, AudioManager.STREAM_MUSIC, 100); 
                    soundPoolMap = new HashMap<Integer, Integer>(); 
                    for (int i=0;i<sonidos.length;i++){
                    	soundPoolMap.put(i, soundPool.load(context, sonidos[i],1)); 
                    }

            } 
    } 
    public void release() { 
            if (soundPool != null) { 
                    soundPool.release(); 
                    soundPool = null; 
                    return; 
            } 
    } 

    public void pausarSonido(int sound){
        if (soundPool != null) { 
        		soundPool.setLoop(sound, 0);
        		soundPool.setVolume(sound, 0f, 0f); 
        }
    }


    public int playSonido(int sound){
    	int aux=-1;
        if (soundPool != null) { 

            AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE); 
            int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC); 
            Integer soundId = soundPoolMap.get(sound); 
            if (soundId != null) { 
            	aux=soundPool.play(soundPoolMap.get(sound), streamVolume, streamVolume, 1, 0, 1f);

            } 
        } 
        return aux;
    }

    public void setEnabled(boolean enabled) { 
            this.enabled = enabled; 
    } 
} 