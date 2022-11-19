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
import java.util.HashMap;
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
    private Logger config_logger = null;

    // Local Loggers
    private static HashMap<String, Logger> loggers = new HashMap<>();
    
    private Config()
    {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + File.separator + "log4j2.xml");
        config_logger = LogManager.getLogger("Config");
        readConfig();
    }
    
    public static Config getInstance()
    {
        if(config_instance == null)
            config_instance = new Config();
        return config_instance;
    }
    
    public static void info(String logger_name, String message)
    {
        Logger log = null;
        if(loggers.containsKey(logger_name))
            log = loggers.get(logger_name);
        else 
        {
            log = LogManager.getLogger(logger_name);
            loggers.put(logger_name, log);
        }
        log.info(message);
    }

    public static void warn(String logger_name, String message)
    {
        Logger log = null;
        if(loggers.containsKey(logger_name))
            log = loggers.get(logger_name);
        else
        {
            log = LogManager.getLogger(logger_name);
            loggers.put(logger_name, log);
        }
        log.warn(message);
    }

    public String getAvocadosURL()
    {
        return avocadoURL;
    }
    
    private void readConfig()
    {
        config_logger.info("Reading config");
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
            config_logger.warn("Config could not be read, using default URL");
            avocadoURL = DEFAULT_URL;
            
            config_logger.warn("Generating config");
            // Writing new default config
            generateConfig();
        }
        catch(IOException ex)
        {
            config_logger.info(ex.getMessage());
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
                config_logger.warn(ex.getMessage());
            }
            catch(NullPointerException ex)
            {
                config_logger.warn(ex.getMessage());
            }
        }
    }
    
    private void generateConfig()
    {
        config_logger.info("Generating config");
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
            config_logger.warn("Unable to create file");
            config_logger.warn(ex.getMessage());
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
                config_logger.warn(ex.getMessage());
            }
        }
    }
    
    public void editURL(URL url)
    {
        avocadoURL = url.toString();
        config_logger.info("Changing avocadoURL to " + avocadoURL);
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
            config_logger.info("Default URL corrupted/invalid");
        }
    }
}
