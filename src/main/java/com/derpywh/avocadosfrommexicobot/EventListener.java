package com.derpywh.avocadosfrommexicobot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventListener extends ListenerAdapter 
{
    Guild currentGuild = null;
    private static boolean isEnabled = true;
    private Config c = null;
    private Logger l = null;
    private String avocadoURL = null;
    private final String DERPY_ID = "105012641159770112";

    public EventListener()
    {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + File.separator + "log4j2.xml");
        c = Config.getInstance();
        l = LogManager.getLogger("EventListener");
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
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        switch(args[0])
        {
            case "!avocados" -> {
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
                            eb.addField("slash", "Internal: Registers slash commands", true);
                            eb.addField("slash_guild", "Internal: Registers slash commands to the guild", true);
                            eb.addField("slash_purge", "Internal: Purges registered slash commands.", true);
                            eb.addField("shutdown", "Internal: Shuts bot down gracefully", true);
                            event.getChannel().asTextChannel().sendMessageEmbeds(eb.build()).queue();
                        }
                        case "slash" -> {
                            JDA jda = event.getJDA();
                            CommandListUpdateAction update = jda.updateCommands();
                            update.addCommands(
                                Commands.slash("avocados", "Plays the Avocados from Mexico jingle in your currently joined voice channel.")
                            );
                            update.queue();
                        }
                        case "slash_guild" -> {
                            CommandListUpdateAction update = event.getGuild().updateCommands();
                            update.addCommands(
                                Commands.slash("avocados", "Plays the Avocados from Mexico jingle in your currently joined voice channel.")
                            );
                            update.queue();
                        }
                        case "slash_purge" -> {
                            CommandListUpdateAction update = event.getGuild().updateCommands();
                            update.queue();
                        }
                        case "shutdown" -> {
                            JDA jda = event.getJDA();
                            jda.shutdown();
                        }
                        default -> {
                            event.getChannel().asTextChannel().sendMessage("Invalid command, try \"help\"").queue();
                        }
                    }
                }
            }
            case "!join" -> {
                currentGuild = event.getGuild();
                VoiceChannel vc;

                List<VoiceChannel> possibleVCs = event.getGuild().getVoiceChannelsByName(args[1], true);
                if(possibleVCs.isEmpty())
                    vc = event.getGuild().getVoiceChannelById(args[1]);
                else
                    vc = possibleVCs.get(0);

                joinVoiceChannel(currentGuild, vc);
            }
        }
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        currentGuild = event.getGuild();
        
        if(event.getMember().getVoiceState().getChannel() != null)
        {
            String reply = "On it! Joining " + event.getMember().getVoiceState().getChannel().getName();
            event.reply(reply).setEphemeral(true).queue();
            joinVoiceChannel(event.getGuild(), (VoiceChannel) event.getMember().getVoiceState().getChannel());
        }
        else event.reply("You're not in a voice channel! Join a voice channel to use this command.").setEphemeral(true).queue();
    }

    private void joinVoiceChannel(Guild g, VoiceChannel vc)
    {
        AudioManager audioManager = g.getAudioManager();
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
