package com.jjcgames.android;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerSoundPoolAdapter implements ISoundPool {

	private ArrayList<MediaPlayer> players = new ArrayList<MediaPlayer>();
	private int streamType;

	public MediaPlayerSoundPoolAdapter(
		int maxStreams, int streamType, int srcQuality) {
		
		this.streamType = streamType;
	}

	@Override
	synchronized public int load(Context context, int resId, int priority) {
		final MediaPlayer player = MediaPlayer.create(context, resId);
		player.setAudioStreamType(streamType);
		players.add(player);
		return players.size() - 1;
	}

	@Override
	public int play(int soundID, float leftVolume, float rightVolume,
		int priority, int loop, float rate) {
		
		final MediaPlayer player = players.get(soundID);
		player.setVolume(leftVolume, rightVolume);
		player.start();
		return 0;
	}

}
