package com.jjcgames.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;

public class SoundPoolProxy implements ISoundPool {

	public static ISoundPool bestSoundPool(
		int maxStreams, int streamType, int srcQuality) {
		
		try {
			return new SoundPoolProxy(maxStreams, streamType, srcQuality);
		} catch (Exception e) {
			Log.v("SoundPoolProxy",
				"Loading SoundPool failed so using MediaPlayer instead.");
			return new MediaPlayerSoundPoolAdapter(
				maxStreams, streamType, srcQuality);
		}
	}

	final private Object sp;
	final private Method loadMethod;
	final private Method playMethod;

	@SuppressWarnings("unchecked")
	public SoundPoolProxy(
		int maxStreams, int streamType, int srcQuality) throws Exception {
		
		final Class spClass = SoundPoolProxy.class.getClassLoader().loadClass(
			"android.media.SoundPool");
		sp = spClass.getConstructor(
			int.class, int.class, int.class).newInstance(
			maxStreams, streamType, srcQuality);
		loadMethod = spClass.getMethod("load",
			Context.class, int.class, int.class);
		playMethod = spClass.getMethod("play",
			int.class, float.class, float.class,
			int.class, int.class, float.class);
	}

	@Override
	public int load(Context context, int resId, int priority) {
		try {
			return ((Integer) loadMethod.invoke(sp,
				context, resId, priority)).intValue();
		} catch (IllegalAccessException e) {
			assert false;
		} catch (InvocationTargetException e) {
			assert false;
		}
		throw new RuntimeException();
	}

	@Override
	public final int play(int soundID, float leftVolume, float rightVolume,
		int priority, int loop, float rate) {
		
		try {
			return ((Integer) playMethod.invoke(sp,
				soundID, leftVolume, rightVolume,
				priority, loop, rate)).intValue();
		} catch (IllegalAccessException e) {
			assert false;
		} catch (InvocationTargetException e) {
			assert false;
		}
		throw new RuntimeException();
	}

}
