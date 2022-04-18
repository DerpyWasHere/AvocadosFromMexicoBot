/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public AudioEventListener(JoinListener j)
    {
        jList = j;
    }
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if(jList != null)
            jList.disconnect();
    }
}
