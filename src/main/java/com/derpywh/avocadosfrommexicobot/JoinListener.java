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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the joining/leaving function of the bot
 * @author DER-PC
 */

public class JoinListener extends ListenerAdapter
{
    Guild currentGuild = null;
    private static boolean isEnabled = true;
    private Config c = null;
    private Logger l = null;
    private String avocadoURL = null;
    private final String TIGER_ID = "118128252081537026";
    
    public JoinListener()
    {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + File.separator + "log4j2.xml");
        c = Config.getInstance();
        l = LogManager.getLogger("JoinListener");
        avocadoURL = c.getAvocadosURL();
        l.info("Using " + avocadoURL + " as avocadoURL");
    }
    
    
    public void disconnect()
    {
        if(currentGuild != null)
        {
            AudioManager audioManager = currentGuild.getAudioManager();
            audioManager.closeAudioConnection();
            l.info("Leaving " + audioManager.getConnectedChannel());
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
                currentGuild = event.getGuild();

                VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection(vc);

                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                AudioSourceManagers.registerRemoteSources(playerManager);
                AudioPlayer player = playerManager.createPlayer();

                player.addListener(new AudioEventListener(this));

                final TrackScheduler scheduler = new TrackScheduler(player);
                playerManager.loadItem(avocadoURL, scheduler);

                SendHandler sH = new SendHandler(player);
                audioManager.setSendingHandler(sH);
                
                l.info("Joining " + vc);
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
                currentGuild = event.getGuild();

                VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection(vc);

                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                AudioSourceManagers.registerRemoteSources(playerManager);
                AudioPlayer player = playerManager.createPlayer();

                player.addListener(new AudioEventListener(this));

                final TrackScheduler scheduler = new TrackScheduler(player);
                playerManager.loadItem(avocadoURL, scheduler);

                SendHandler sH = new SendHandler(player);
                audioManager.setSendingHandler(sH);
                
                l.info("Joining " + vc);
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
}
