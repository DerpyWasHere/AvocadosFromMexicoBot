/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.derpywh.avocadosfrommexicobot;

/**
 *
 * @author DER-PC
 */
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;     
import net.dv8tion.jda.api.JDABuilder;
public class AvocadosFromMexico 
{
    public static void main(String[] args)
    {
        try
        {
            JDABuilder builder = JDABuilder.createDefault(args[0]);
            JDA jda = builder.build();
            jda.addEventListener(new JoinListener());
            jda.awaitReady();
        }
        catch(LoginException loginEx)
        {
            loginEx.printStackTrace();
        }
        catch(InterruptedException interruptedEx)
        {
            interruptedEx.printStackTrace();
        }
    }
}
