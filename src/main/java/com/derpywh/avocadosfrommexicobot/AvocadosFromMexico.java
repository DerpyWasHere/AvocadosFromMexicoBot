package com.derpywh.avocadosfrommexicobot;

/**
 * Entry point for the bot
 * @author DER-PC
 */
import net.dv8tion.jda.api.JDA;     
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
public class AvocadosFromMexico 
{
    public static void main(String[] args)
    {
        try
        {
            JDABuilder builder = JDABuilder.createDefault(args[0]);
            builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
            JDA jda = builder.build();
            jda.addEventListener(new JoinListener());
            jda.addEventListener(new EventListener());
            jda.awaitReady();
        }
        catch(InterruptedException interruptedEx)
        {
            interruptedEx.printStackTrace();
        }
    }
}
