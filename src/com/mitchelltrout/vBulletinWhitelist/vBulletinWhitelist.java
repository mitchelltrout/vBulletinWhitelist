package com.mitchelltrout.vBulletinWhitelist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
* vBulletinWhitelist plugin for Bukkit
*
* @author Mitchell Trout
*/
public class vBulletinWhitelist extends JavaPlugin {
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public static Logger Log = Logger.getLogger("Minecraft");
    private final VbwPlayerListener playerListener = new VbwPlayerListener(this);
    public static PluginDescriptionFile pdfFile;
    public static String forumpath = "";
    public static String forumsys = "";
    public static String profilefield = "";
    public static Boolean strictmode = false;

    public void onDisable() {
    	pdfFile = this.getDescription();
        Log.log(Level.INFO, "[" + pdfFile.getName() + "] version " + pdfFile.getVersion() + " is disabled!");
    }

    public void onEnable() {
    	
    	pdfFile = this.getDescription();
    	
    	if(!new File("plugins/vBulletinWhitelist/vBulletinWhitelist.properties").exists())
        {
            writeConfig();
        }
    	
    	try
        {
            
        }
        catch(Exception e)
        {
            Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Exception while reading vBulletinWhitelist.properties");
            e.printStackTrace();
        }
    	
    	
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);

        Log.log(Level.INFO, "[" + pdfFile.getName() + "] version " + pdfFile.getVersion() + " is enabled!");
    }
    
    public static int isMember(String playername)
    {
    	int member = 0; //0=false, 1=true, 2=error
    	
    	Log.log(Level.INFO, "[" + pdfFile.getName() + "] " + playername + " is attempting to join server...");
    	
    	try {
    		
    		URL address = new URL(forumpath + "mcwhitelist.php?fsys=" + forumsys + "&pf=" + profilefield + "&user=" + playername);
			URLConnection yc = address.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				
				if(inputLine.equals("true"))
				{
					Log.log(Level.INFO, "[" + pdfFile.getName() + "] " + playername + " is registered.");
					member = 1;
				}
				else if(inputLine.equals("false"))
				{
					Log.log(Level.INFO, "[" + pdfFile.getName() + "] " + playername + " is not registered. DENIED!");
					member = 0;
				}
				else
				{
					member = 2; //anything else means error in php
					Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Webserver gave unknown response.");
				}

			}
			in.close();
		} catch (Exception e) {
			Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Exception while trying to read " + forumpath + "mcwhitelist.php");
			
			if(strictmode)
				member = 2;
			else
				member = 1;
			
			e.printStackTrace();
		}

    	return member;
    }
    
    public static void writeConfig()
    {
    	BufferedWriter Writer = null;
    	
    	try
        {
    		new File("plugins/vBulletinWhitelist/").mkdir();
        	new File("plugins/vBulletinWhitelist/vBulletinWhitelist.properties").createNewFile();
            Writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("plugins/vBulletinWhitelist/vBulletinWhitelist.properties")));
            Writer.write("# The path to your forums directory. Please include the trailing slash.\r\nforumpath=http://www.yourwebsite.com/path/to/forum/\r\n");
            Writer.write("# Forum System. vBulletin=1, MyBB=2, phpBB=3\r\nforumsys=0\r\n");
            Writer.write("# Database profile field name.\r\nprofilefield=minecraftUsernameFieldID\r\n");
            Writer.write("# If strictmode is false plugin will allow connections when it can not connect to forum.\r\nstrictmode=true\r\n");
            Writer.flush();
            Writer.close();
            Log.log(Level.INFO, "[" + pdfFile.getName() + "] vBulletinWhitelist.properties created");
        }
        catch(Exception e)
        {
            Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Exception while creating vBulletinWhitelist.properties");
            e.printStackTrace();
        }
    }
    
    public static void readConfig()
    {
    	int varcount = 0;
    	
    	try
        {
    		Scanner scan = new Scanner(new FileInputStream("plugins/vBulletinWhitelist/vBulletinWhitelist.properties"));
            String input, var, val;
            while(scan.hasNextLine())
            {
                input = scan.nextLine();

                if(input.startsWith("#")) continue;

                var = input.substring(0, input.indexOf("=")).toLowerCase();
                val = input.substring(input.indexOf("=") + 1);

                if(var.equals("forumpath"))
                {
                    forumpath = val;
                    varcount++;
                }               
                else if(var.equals("forumsys"))
                {
                    forumsys = val;
                    varcount++;
                }                
                else if(var.equals("profilefield"))
                {
                    profilefield = val;
                    varcount++;
                }
                else if(var.equals("strictmode"))
                {
                    if(val.equals("false"))
                    	strictmode = false;
                    else
                    	strictmode = true;
                    
                    varcount++;
                }
            }
            
            scan.close();
        }
        catch(Exception e)
        {
            Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Exception while reading vBulletinWhitelist.properties");
            e.printStackTrace();
        }
        
        if(varcount < 4) //not all config settings were read. the plugin will not work properly.
        {
        	Log.log(Level.WARNING, "[" + pdfFile.getName() + "] MISSING CONFIG SETTINGS. WRITING NEW CONFIG FILE.");
        	if(new File("plugins/vBulletinWhitelist/vBulletinWhitelist.properties").delete() && new File("plugins/vBulletinWhitelist/").delete())
        	{
        		writeConfig();
        		Log.log(Level.WARNING, "[" + pdfFile.getName() + "] YOU MUST EDIT THE NEW CONFIG SETTINGS AND RESTART SERVER FOR PLUGIN TO WORK!");
        	}
        	else
        	{
        		Log.log(Level.WARNING, "[" + pdfFile.getName() + "] COULD NOT DELETE OLD CONFIG.");
        	}
        	
        }
    }
    
    public static String getForumPath()
    {
    	return forumpath;
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}