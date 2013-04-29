package com.jjcgames.android;

import android.content.Context;

public interface ISoundPool {
	public int load(Context context, int resId, int priority);
	public int play(int soundID, float leftVolume, float rightVolume,
		int priority, int loop, float rate);
};
