package com.derpywh.avocadosfrommexicobot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.io.File;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * Handles the joining/leaving function of the bot
 * @author DER-PC
 */

public class JoinListener extends ListenerAdapter
{
    private static boolean isEnabled = true;
    private String avocadoURL = null;
    private final String TIGER_ID = "118128252081537026";
    
    public JoinListener()
    {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + File.separator + "log4j2.xml");
        avocadoURL = Config.getInstance().getAvocadosURL();
        Config.info("JoinListener", "Using " + avocadoURL + " as avocadoURL");
    }
    
    
    public void disconnect(Guild guild)
    {
        if(guild != null)
        {
            AudioManager audioManager = guild.getAudioManager();
            audioManager.closeAudioConnection();
            Config.info("JoinListener", "Leaving " + audioManager.getConnectedChannel());
        }
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event)
    {
        if(event.getChannelJoined() == null)
            onGuildVoiceLeave(event);
        else if(event.getChannelLeft() == null)
            onGuildVoiceJoin(event);
        else
            onGuildVoiceMove(event);
    }


    private void onGuildVoiceJoin(GuildVoiceUpdateEvent event)
    {
        if(event.getMember().getId().equals(TIGER_ID))
        {
            event.getMember().getUser().openPrivateChannel()
                    .flatMap(ch -> ch.sendMessage("Avocados :avocado: from Mexico :flag_mx:"))
                    .queue();
            if(isEnabled)
            {
                joinVoiceChannel(event.getGuild(), (VoiceChannel) event.getVoiceState().getChannel());
            }
        }
        
        
    }
    
    private void onGuildVoiceMove(GuildVoiceUpdateEvent event)
    {
        if(event.getMember().getId().equals(TIGER_ID))
        {
            event.getMember().getUser().openPrivateChannel()
                    .flatMap(ch -> ch.sendMessage("Avocados :avocado: from Mexico :flag_mx:"))
                    .queue();
            
            if(isEnabled)
            {
                joinVoiceChannel(event.getGuild(), (VoiceChannel) event.getMember().getVoiceState().getChannel());
            }
        }
    }
    
    private void onGuildVoiceLeave(GuildVoiceUpdateEvent event)
    {
        if(event.getMember().getId().equals(TIGER_ID))
        {
            event.getMember().getUser().openPrivateChannel()
                    .flatMap(ch -> ch.sendMessage("Avocados :avocado: from Mexico :flag_mx:"))
                    .queue();
        }
    }

    private void joinVoiceChannel(Guild g, VoiceChannel vc)
    {
        AudioManager audioManager = g.getAudioManager();
        audioManager.openAudioConnection(vc);
        
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();

        player.addListener(new AudioEventListener(this, g));

        final TrackScheduler scheduler = new TrackScheduler(player);
        playerManager.loadItem(avocadoURL, scheduler);

        SendHandler sH = new SendHandler(player);
        audioManager.setSendingHandler(sH);
        
        Config.info("EventListener", "Joining " + vc);
    }
}
