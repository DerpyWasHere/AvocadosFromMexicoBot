/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.derpywh.avocadosfrommexicobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author DER-PC
 */
public class Config {
    // Constants
    final String DEFAULT_URL = "https://www.youtube.com/watch?v=xgTiKlwB-Uk";
    
    // Singleton instance
    private static Config config_instance = null;
    
    // Config variables
    private String avocadoURL = "";
    private Logger l = null;
    
    private Config()
    {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + File.separator + "log4j2.xml");
        l = LogManager.getLogger("Config");
        readConfig();
    }
    
    public static Config getInstance()
    {
        if(config_instance == null)
            config_instance = new Config();
        return config_instance;
    }
    
    public String getAvocadosURL()
    {
        return avocadoURL;
    }
    
    private void readConfig()
    {
        l.info("Reading config");
        FileReader fr = null;
        BufferedReader br = null;
        try
        {
            File f = new File("config.ini");
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            String s;
            s = br.readLine();
            System.out.println(s);
            avocadoURL = s.substring(4, s.length());
            System.out.println(avocadoURL);
        }
        catch(FileNotFoundException ex)
        {
            l.warn("Config could not be read, using default URL");
            avocadoURL = DEFAULT_URL;
            
            l.warn("Generating config");
            // Writing new default config
            generateConfig();
        }
        catch(IOException ex)
        {
            l.info(ex.getMessage());
        }
        finally
        {
            try
            {
                fr.close();
                br.close();
            }
            catch(IOException ex)
            {
                l.warn(ex.getMessage());
            }
            catch(NullPointerException ex)
            {
                l.warn(ex.getMessage());
            }
        }
    }
    
    private void generateConfig()
    {
        l.info("Generating config");
        File f = new File("config.ini");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try
        {
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            
            bw.write("url=" + avocadoURL);
            bw.newLine();
        }
        catch(IOException ex)
        {
            l.warn("Unable to create file");
            l.warn(ex.getMessage());
        }
        finally
        {
            try
            {
                bw.close();
                fw.close();
            }
            catch(IOException ex)
            {
                l.warn(ex.getMessage());
            }
        }
    }
    
    public void editURL(URL url)
    {
        avocadoURL = url.toString();
        l.info("Changing avocadoURL to " + avocadoURL);
        generateConfig();
    }
    
    public void defaultURL() 
    {
        try
        {
            editURL(new URL(DEFAULT_URL));
        }
        catch(MalformedURLException ex)
        {
            l.info("Default URL corrupted/invalid");
        }
    }
}
