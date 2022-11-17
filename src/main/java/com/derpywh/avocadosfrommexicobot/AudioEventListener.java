package com.derpywh.avocadosfrommexicobot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

/**
 *
 * @author DER-PC
 */
public class AudioEventListener extends AudioEventAdapter
{
    JoinListener jList = null;
    EventListener eList = null;
    public AudioEventListener(JoinListener j)
    {
        jList = j;
    }

    public AudioEventListener(EventListener e)
    {
        eList = e;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if(jList != null)
            jList.disconnect();
        if(eList != null)
            eList.disconnect();
    }
}
