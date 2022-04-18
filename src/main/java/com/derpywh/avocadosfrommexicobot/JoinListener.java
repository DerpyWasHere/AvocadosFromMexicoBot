/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.derpywh.avocadosfrommexicobot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author DER-PC
 */

public class JoinListener extends ListenerAdapter
{
    Guild currentGuild = null;
    private static boolean isEnabled = true;
    private Config c = null;
    private Logger l = null;
    private String avocadoURL = null;
    private final String TIGER_ID = "118128252081537026",
                         DERPY_ID = "105012641159770112";
    
    public JoinListener()
    {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + File.separator + "log4j2.xml");
        c = Config.getInstance();
        l = LogManager.getLogger("JoinListener");
        avocadoURL = c.getAvocadosURL();
        l.info("Using " + avocadoURL + " as avocadoURL");
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        switch(args[0])
        {
            case "!avocados" -> {
                currentGuild = event.getGuild();
            
                VoiceChannel vc = event.getMember().getVoiceState().getChannel();
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
            case "!toggle" -> {
                if(isEnabled)
                {
                    isEnabled = false;
                    event.getChannel().sendMessage("Avocados disabled").queue();
                }
                else 
                {
                    isEnabled = true;
                    event.getChannel().sendMessage("Avocados enabled").queue();
                }
            }
            case "!editconf" -> {
                if(event.getMessage().getAuthor().getId().equals(DERPY_ID))
                {
                    switch(args[1])
                    {
                        case "url" -> {
                            try
                            {
                                c.editURL(new URL(args[2]));
                                avocadoURL = c.getAvocadosURL();
                                event.getChannel().sendMessage("Changed Avocados URL to " + avocadoURL).queue();
                            }
                            catch(MalformedURLException ex)
                            {
                                l.warn("Invalid URL, please check for any errors in your URL.");
                                event.getChannel().sendMessage("Invalid URL, please check for any errors in your URL.").queue();
                            }
                        }
                        case "dc" -> {
                            disconnect();
                        }
                        case "reset_url" -> {
                            c.defaultURL();
                            l.info("Reset URL called");
                            event.getChannel().sendMessage("Resetting Avocados URL").queue();
                            avocadoURL = c.getAvocadosURL();
                        }
                        case "help" -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Avocados :avocado: from Mexico :flag_mx:");
                            eb.setColor(new Color(0xD016F5));
                            eb.setImage("https://derpywashere.s-ul.eu/8xdQBk1C");
                            eb.addField("url", "Changes the current AvocadosFromMexicoBot sound clip", true);
                            eb.addField("dc", "Disconnects AvocadosFromMexicoBot from its current voice channel", true);
                            eb.addField("reset_url", "Resets the current url to a predefined default", true);
                            eb.addField("help", "Displays this help command", true);
                            event.getChannel().sendMessage(eb.build()).queue();
                        }
                        default -> {
                            event.getChannel().sendMessage("Invalid command, try \"help\"").queue();
                        }
                    }
                }
            }
            case "!join" -> {
                currentGuild = event.getGuild();
                
                VoiceChannel vc = event.getGuild().getVoiceChannelById(args[1]);
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
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
    {
        if(event.getMember().getId().equals(TIGER_ID))
        {
            event.getMember().getUser().openPrivateChannel()
                    .flatMap(ch -> ch.sendMessage("Avocados :avocado: from Mexico :flag_mx:"))
                    .queue();
            if(isEnabled)
            {
                currentGuild = event.getGuild();

                VoiceChannel vc = event.getMember().getVoiceState().getChannel();
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
    
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event)
    {
        if(event.getMember().getId().equals(TIGER_ID))
        {
            event.getMember().getUser().openPrivateChannel()
                    .flatMap(ch -> ch.sendMessage("Avocados :avocado: from Mexico :flag_mx:"))
                    .queue();
            
            if(isEnabled)
            {
                currentGuild = event.getGuild();

                VoiceChannel vc = event.getMember().getVoiceState().getChannel();
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
    
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
    {
        if(event.getMember().getId().equals(TIGER_ID))
        {
            event.getMember().getUser().openPrivateChannel()
                    .flatMap(ch -> ch.sendMessage("Avocados :avocado: from Mexico :flag_mx:"))
                    .queue();
        }
    }
}
