package com.derpywh.avocadosfrommexicobot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

/**
 *
 * @author DER-PC
 */
public class AudioEventListener extends AudioEventAdapter
{
    JoinListener jList = null;
    EventListener eList = null;
    Guild guild = null;
    public AudioEventListener(JoinListener j, Guild g)
    {
        jList = j;
        guild = g;
    }

    public AudioEventListener(EventListener e, Guild g)
    {
        eList = e;
        guild = g;

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if(jList != null)
            jList.disconnect(guild);
        if(eList != null)
            eList.disconnect(guild);
            
    }
}
